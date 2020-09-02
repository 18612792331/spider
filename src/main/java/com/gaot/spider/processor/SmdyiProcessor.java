package com.gaot.spider.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Collections;
import java.util.List;

public class SmdyiProcessor implements PageProcessor {

    private String baseUrl = "http://www.smdyi.com";

    private Site site = Site.me().setRetryTimes(5).setCycleRetryTimes(3).setSleepTime(500).setTimeOut(10000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
    @Override
    public void process(Page page) {
//        http://www.smdyi.com/search.php?searchtype=5&tid=2
        if (page.getUrl().regex("http://www\\.smdyi\\.com/search\\.php(.+)").match()) {
            System.out.println("第一层 ：" + page.getUrl().toString());
            dyList(page);
        //http://www.smdyi.com/dsj/tianlongbabuyueyu/
        } else if (page.getUrl().regex("http://www\\.smdyi\\.com/dsj(.+)").match()) {
            dyDetail(page);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void dyList(Page page) {
        System.out.println(page.getHtml());
        List<String> all = page.getHtml().xpath("//li[@class='mb']//div[@class='text]//a/@href").all();
        Collections.reverse(all);
        for (String href:all) {
            page.addTargetRequest(new Request(baseUrl + href).setPriority(1));

        }

    }

    public void dyDetail(Page page) {
        System.out.println(page.getHtml());

    }

    public static void main(String[] args) {
        String url = "http://www.smdyi.com/search.php?page=2&searchtype=5&tid=2";
        Spider.create(new SmdyiProcessor()).addUrl(url).run();

    }
}
