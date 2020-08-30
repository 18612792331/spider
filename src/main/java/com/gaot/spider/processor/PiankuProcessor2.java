package com.gaot.spider.processor;

import com.gaot.spider.domin.MediaData;
import com.gaot.spider.download.PiankuDownloader;
import com.gaot.spider.resource.utils.HtmlunitUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

public class PiankuProcessor2 implements PageProcessor {

    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    private Integer i = 1;

    private String baseUrl = "https://www.subofm.com";

    private String resourceUrl = "https://www.pianku.tv/ajax/downurl/";

    private Site site = Site.me().setRetryTimes(5).setSleepTime(1000).setTimeOut(30000)
            .addCookie("Hm_lvt_5a21a69d1b034aed24dcda25771e8135", "1597749684")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
    @Override
    public void process(Page page) {
        System.out.println("url=" + page.getUrl());
        //https://www.subofm.com/index.php/vod/show/id/1.html
        if (page.getUrl().regex("https://www\\.subofm\\.com/index\\.php/vod/show/(.+)html").match()) {
            System.out.println("第一层 ：" + page.getUrl().toString());
            dyList(page);

        } else if (page.getUrl().regex("https://www\\.subofm\\.com/index\\.php/vod/detail/(.+)html").match()){
            System.out.println("第二层：" + page.getUrl().toString());
            dyDetail(page);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    // 爬取电影列表页
    public void dyList(Page page) {
        List<String> all = page.getHtml().xpath("//a[@class='fed-list-title fed-font-xiv fed-text-center fed-text-sm-left fed-visible fed-part-eone']/@href").all();
        all.forEach(data->{
            String url = baseUrl + data;

            page.addTargetRequest(new Request(url).setPriority(1));

        });
    }

    public void dyDetail(Page page) {

        MediaData mediaData = new MediaData();
        System.out.println("----------------------------------" + i + "--------------------------------------------");
        String uri = page.getHtml().xpath("//div[@class='fed-tabs-boxs']//div[@class='fed-tabs-item fed-drop-info fed-visible']//div[@class='fed-drop-boxs fed-drop-btms fed-matp-v']//ul[@class='fed-part-rows']/li[1]//a/@href").toString();
//        String name = page.getHtml().xpath("//div[@class='fed-tabs-boxs']").toString();
        System.out.println(baseUrl+uri);
        try {
            String htmlPageResponse = HtmlunitUtils.getHtmlPageResponse(baseUrl + uri);
            Document doc = Jsoup.parse(htmlPageResponse);
            String iframe = doc.select("iframe").toString();
            System.out.println("==============================================================================================================");
            System.out.println(iframe);

        } catch (Exception e) {
            e.printStackTrace();
        }

        i++;
    }

    public static void main(String[] args) {
        String path = "https://www.subofm.com/index.php/vod/show/id/1/page/1.html";
        Spider.create(new PiankuProcessor2()).setDownloader(new PiankuDownloader()).addUrl(path).thread(1).run();
    }
}
