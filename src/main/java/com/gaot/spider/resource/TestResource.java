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

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class TestResource {
    @Autowired
    private MongoTemplate mongoTemplate;

    public static void main(String[] args) {
        String aa = "https://www.pianku.tv/ajax/downurl/wNjRDZrhja_mv";
        String pattern = "https://www\\.pianku\\.tv/ajax/downurl/(.{10})_mv";
        boolean isMatch = Pattern.matches(pattern, aa);
        System.out.println(isMatch);
    }
    String url="https://www.pianku.tv/mv/------1.html";

    @PostMapping("/test")
    public void test() {
        PiankuProcessor processor = new PiankuProcessor();
        processor.setMongoTemplate(mongoTemplate);
        Spider.create(processor).setDownloader(new PiankuDownloader()).addPipeline(new PiankuPipeline()).addUrl(url).thread(1).run();
    }

}
