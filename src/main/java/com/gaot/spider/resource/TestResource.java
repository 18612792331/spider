package com.gaot.spider.resource;

import com.gaot.spider.domin.MediaData;
import com.gaot.spider.download.Downloader;
import com.gaot.spider.processor.SmdyiProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class TestResource {
    @Autowired
    private MongoTemplate mongoTemplate;


    @GetMapping("/test")
    public void test() throws Exception {
        String url="http://www.smdyi.com/search.php?page=401&searchtype=5&tid=2";
        Downloader downloader = new Downloader();
        SmdyiProcessor processor = new SmdyiProcessor();
        processor.setMongoTemplate(mongoTemplate);
        processor.setType(2);
        processor.setCount(400);

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
