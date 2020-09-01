package com.gaot.spider.resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gaot.spider.download.Downloader;
import com.gaot.spider.processor.AppMovieProcessor;
import com.github.kevinsawicki.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

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
    String url="https://app.movie/index.php/vod/type/id/2/page/643.html";

    @PostMapping("/test")
    public void test() {
        Proxy[] ips=null;
        String response = HttpRequest.get("http://d.jghttp.golangapi.com/getip?num=200&type=2&pro=110000&city=110105&yys=0&port=1&pack=29459&ts=1&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=").body();
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        ips = new Proxy[jsonArray.size()];
        List<Map<String, Object>> list = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject data = (JSONObject) jsonArray.get(i);
            System.out.println(data.getString("ip"));
            System.out.println(data.getInteger("port"));
            ips[i] = new Proxy(data.getString("ip"), data.getInteger("port"));
            Map<String, Object> map = new HashMap<>();
            map.put("ip", data.getString("ip"));
            map.put("port", data.getInteger("port"));
            list.add(map);
        }
        System.out.println("size:" + list.size());



        Downloader downloader = new Downloader();

        downloader.setProxyProvider(SimpleProxyProvider.from(ips));
        AppMovieProcessor processor = new AppMovieProcessor();
        processor.setMongoTemplate(mongoTemplate);
        processor.setType(2);
        processor.setCount(642);
        processor.setIpPools(list);
        Spider.create(processor).setDownloader(downloader).addUrl(url).thread(2).run();
    }

}
