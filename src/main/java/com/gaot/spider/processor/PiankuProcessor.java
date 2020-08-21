package com.gaot.spider.processor;

import com.gaot.spider.download.PiankuDownloader;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

public class PiankuProcessor implements PageProcessor {

    private String baseUrl = "https://www.pianku.tv";

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent("User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0");
    @Override
    public void process(Page page) {
//        page.putField("detailPath", );
//        List<String> all = page.getHtml().$(".li-img cover").$("a", "href").all();
        List<String> all = page.getHtml().xpath("//div[@class='li-img cover']").all();
        System.out.println(all.size());
        all.forEach(data->{
            System.out.println("-------------------------------------------------" + data);
        });


    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String url="https://www.pianku.tv/mv/------1.html";
        Spider.create(new PiankuProcessor()).setDownloader(new PiankuDownloader()).addUrl(url).thread(1).run();
    }
}
