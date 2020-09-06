package com.gaot.spider.resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gaot.spider.domin.MediaData;
import com.gaot.spider.download.Downloader;
import com.gaot.spider.processor.AppMovieProcessor;
import com.gaot.spider.processor.SmdyiProcessor;
import com.gaot.spider.resource.utils.JsoupUtils;
import com.gaot.spider.resource.utils.SslUtils;
import com.github.kevinsawicki.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class TestResource {
    @Autowired
    private MongoTemplate mongoTemplate;


    @GetMapping("/test")
    public void test() throws Exception {
        String url="http://www.smdyi.com/search.php?page=228&searchtype=5&tid=4";
        Downloader downloader = new Downloader();
        SmdyiProcessor processor = new SmdyiProcessor();
        processor.setMongoTemplate(mongoTemplate);
        processor.setType(4);
        processor.setCount(227);

        Spider.create(processor).setDownloader(downloader).addUrl(url).run();
    }

    @PostMapping("/settime")
    public void setTime() {
        List<MediaData> all = mongoTemplate.findAll(MediaData.class);
        all.forEach(mediaData -> {
            mediaData.setCreateTime(LocalDateTime.now());
            mongoTemplate.save(mediaData);
        });
    }




}
