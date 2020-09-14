package com.gaot.spider.resource;

import com.gaot.spider.domin.MediaData;
import com.gaot.spider.download.Downloader;
import com.gaot.spider.processor.SmdyiProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    @PostMapping("/update")
    public void setTime() {
        System.out.println("更新");
        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(4));

        List<MediaData> all = mongoTemplate.find(query, MediaData.class);
        System.out.println(all.size());
        int i = 0;
        for (MediaData mediaData:all) {
            if (StringUtils.isNotBlank(mediaData.getArea())) {
                mediaData.setArea(mediaData.getArea().trim());
                mongoTemplate.save(mediaData);
            }
            System.out.println(mediaData.toString());
            System.out.println("=========================" + i + "=========================");
            i++;
        }

    }




}
