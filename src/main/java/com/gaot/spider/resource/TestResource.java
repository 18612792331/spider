package com.gaot.spider.resource;

import com.gaot.spider.download.PiankuDownloader;
import com.gaot.spider.pipeline.PiankuPipeline;
import com.gaot.spider.processor.AppMovieProcessor;
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
//        https://app.movie/index.php/vod/type/id/1/page/1.html
        String aa = "https://app.movie/index.php/vod/type/id/1/page/1.html";
        String pattern = "https://app\\.movie/index\\.php/vod/type/id/(.+)html";
        boolean isMatch = Pattern.matches(pattern, aa);
        System.out.println(isMatch);
    }
    String url="https://app.movie/index.php/vod/type/id/2/page/643.html";

    @PostMapping("/test")
    public void test() {
        AppMovieProcessor processor = new AppMovieProcessor();
        processor.setMongoTemplate(mongoTemplate);
        processor.setType(2);
        processor.setCount(642);
        Spider.create(processor).setDownloader(new PiankuDownloader()).addUrl(url).thread(1).run();
    }

}
