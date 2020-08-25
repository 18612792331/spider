package com.gaot.spider.resource;

import com.gaot.spider.download.PiankuDownloader;
import com.gaot.spider.pipeline.PiankuPipeline;
import com.gaot.spider.processor.PiankuProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;

@RestController
@RequestMapping("/api")
public class TestResource {
    @Autowired
    private MongoTemplate mongoTemplate;

    public static void main(String[] args) {
        System.out.println(2%3);
    }
    String url="https://www.pianku.tv/mv/------1.html";

    @PostMapping("/test")
    public void test() {
        PiankuProcessor processor = new PiankuProcessor();
        processor.setMongoTemplate(mongoTemplate);
        Spider.create(processor).setDownloader(new PiankuDownloader()).addPipeline(new PiankuPipeline()).addUrl(url).thread(1).run();
    }

}
