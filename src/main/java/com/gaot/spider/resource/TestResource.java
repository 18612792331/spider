package com.gaot.spider.resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gaot.spider.download.Downloader;
import com.gaot.spider.processor.AppMovieProcessor;
import com.gaot.spider.resource.utils.JsoupUtils;
import com.github.kevinsawicki.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class TestResource {
    @Autowired
    private MongoTemplate mongoTemplate;

    public static void main(String[] args) {
//        https://app.movie/index.php/vod/type/id/1/page/1.html
        String aa = "https://app.movie/index.php/vod/type/id/1/page/1.html";
        String pattern = "https://app\\.movie/index\\.php/vod/type/id/(.+)html";
        boolean isMatch = Pattern.matches(pattern, aa);
        System.out.println(isMatch);
    }
    String url="https://app.movie/index.php/vod/type/id/2/page/631.html";

    @PostMapping("/test")
    public void test() {
        Proxy[] ips=null;
        String response = HttpRequest.get("http://d.jghttp.golangapi.com/getip?num=200&type=2&pro=110000&city=110105&yys=0&port=1&pack=29459&ts=1&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=").body();
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        ips = new Proxy[jsonArray.size()];
        List<Map<String, Object>> list = new ArrayList<>(jsonArray.size());
        Document doc = null;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject data = (JSONObject) jsonArray.get(i);
            System.out.println(data.getString("ip"));
            System.out.println(data.getInteger("port"));
            try {
                doc = Jsoup.connect("https://app.movie/")
                        .proxy(data.getString("ip"), data.getInteger("port"))
                        .ignoreContentType(true).validateTLSCertificates(false)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36")
                        .timeout(1000*5).get();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (StringUtils.isNotBlank(doc.html())) {
                ips[i] = new Proxy(data.getString("ip"), data.getInteger("port"));
                Map<String, Object> map = new HashMap<>();
                map.put("ip", data.getString("ip"));
                map.put("port", data.getInteger("port"));
                list.add(map);
            }

        }
        System.out.println("size:" + list.size());
        JsoupUtils.ipPools = list;



        Downloader downloader = new Downloader();

        downloader.setProxyProvider(SimpleProxyProvider.from(ips));
        AppMovieProcessor processor = new AppMovieProcessor();
        processor.setMongoTemplate(mongoTemplate);
        processor.setType(2);
        processor.setCount(630);
        processor.setIpPools(list);
        Spider.create(processor).setDownloader(downloader).addUrl(url).run();
    }

}
