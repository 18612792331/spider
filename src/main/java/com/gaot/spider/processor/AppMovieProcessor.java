package com.gaot.spider.processor;

import com.gaot.spider.download.PiankuDownloader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * @program: spider
 * @description:
 * @author: Mr.Gaot
 * @create: 2020-08-28 11:13
 **/

@Component
public class AppMovieProcessor implements PageProcessor {

    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    private Integer i = 1;

    private String baseUrl = "https://app.movie";
    private Site site = Site.me().setRetryTimes(5).setSleepTime(200).setTimeOut(10000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
    @Override
    public void process(Page page) {
        if (page.getUrl().regex("https://app\\.movie/index\\.php/vod/show/(.+)html").match()) {
            System.out.println("第一层 ：" + page.getUrl().toString());
            dyList(page);

        } else if (page.getUrl().regex("https://app\\.movie/index\\.php/vod/detail/(.+)html").match()) {
            dyDetail(page);
        } else if (page.getUrl().regex("https://app\\.movie/index\\.php/vod/play/(.+)html").match()) {
            dyPlay(page);
        }
    }

    public void dyPlay(Page page) {
        Selectable links = page.getHtml().xpath("//div[@class='stui-player__video embed-responsive embed-responsive-16by9 clearfix']//script[@type='text/javascript']");

        System.out.println("====================================================");
//        System.out.println(links.regex("http(.+)m3u8").toString());
        System.out.println(links.toString());
    }

    public void dyDetail(Page page) {
        String uri = page.getHtml().xpath("//div[@class='playbtn']/a/@href").toString();
        System.out.println("uri=" + uri);
        page.addTargetRequest(new Request(baseUrl+uri).setPriority(2));

    }

    // 爬取电影列表页
    public void dyList(Page page) {
        List<String> all = page.getHtml().xpath("//a[@class='stui-vodlist__thumb lazyload']/@href").all();
        all.forEach(data->{
            String url = baseUrl + data;
            page.addTargetRequest(new Request(url).setPriority(1));
        });
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String path = "https://app.movie/index.php/vod/show/by/time/id/1.html";
        Spider.create(new AppMovieProcessor()).setDownloader(new PiankuDownloader()).addUrl(path).thread(1).run();
    }
}
