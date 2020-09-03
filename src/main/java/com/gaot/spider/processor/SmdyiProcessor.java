package com.gaot.spider.processor;

import com.gaot.spider.domin.MediaData;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

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
        } else if (page.getUrl().regex("http://www\\.smdyi\\.com/(dsj|dm)(.+)").match()) {
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
        MediaData mediaData = new MediaData();
        String cover = page.getHtml().xpath("//div[@class='pic']/img/@src").toString();
        if (StringUtils.isNotBlank(cover)) mediaData.setCover(cover.trim());
        System.out.println("封面：" + cover);
        String name = page.getHtml().xpath("//div[@class='info']/dl/dt[@class='name']/h1/a/text()").toString();
        if (StringUtils.isNotBlank(name)) mediaData.setName(name.trim());
        System.out.println("名称：" + name);
        String state = page.getHtml().xpath("//div[@class='info']/dl/dd/font/text()").toString();
        if (StringUtils.isNotBlank(state)) mediaData.setState(state.trim());
        System.out.println("状态：" + mediaData.getState());
        String alias = page.getHtml().xpath("//div[@class='info']/dl/div/dd/allText()").regex(".*别名.+").toString();
        if (StringUtils.isNotBlank(alias)) mediaData.setAlias(alias.replaceAll("别名", "").replaceAll(":", "").trim());
        System.out.println("别名:" + mediaData.getAlias());
        String area = page.getHtml().xpath("//div[@class='info']/dl/dd[2]/span[1]/a[1]/text()").toString();
        if (StringUtils.isNotBlank(area)) mediaData.setArea(area.replaceAll("剧", ""));
        System.out.println("地区：" + mediaData.getArea());
        List<String> genreList = page.getHtml().xpath("//div[@class='info']/dl/dd[2]/span[1]//a/text()").all();
        if (genreList.size()>0) {
            String genre = "";
            for (int i=0; i<genreList.size(); i++) {
                if (i!=0) genre+=(genreList.get(i)+" ");

            }
            if (StringUtils.isNotBlank(genre)) mediaData.setGenre(genre.trim());
            System.out.println("类型：" + mediaData.getGenre());
        }


        String year = page.getHtml().xpath("//div[@class='info']/dl/dd[2]/span[2]/tidyText()").toString();
        if (StringUtils.isNotBlank(year)) {
            year=year.replaceAll("年", "");
            if (year.trim().matches("\\d+")) mediaData.setYear(Integer.valueOf(year.trim()));
        }
        System.out.println("年份：" + mediaData.getYear());
        String director = page.getHtml().xpath("//div[@class='info']/dl/div[2]/dd/text()").toString();
        if (StringUtils.isNotBlank(director)) mediaData.setDirector(director.trim());
        System.out.println("导演：" + mediaData.getDirector());
        List<String> actors = page.getHtml().xpath("//div[@class='info']/dl/dt[@class='desd']//a/text()").all();
        if (actors.size()>0) mediaData.setActor(actors);
        System.out.println("主演：" + mediaData.getActor().toString());
        String laguage = page.getHtml().xpath("//div[@class='info']/dl/div[3]/dd/text()").toString();
        if (StringUtils.isNotBlank(laguage)) mediaData.setLanguage(laguage);
        System.out.println("语言：" + laguage);



    }

    public static void main(String[] args) {
        String url = "http://www.smdyi.com/search.php?page=2&searchtype=5&tid=2";
        Spider.create(new SmdyiProcessor()).addUrl(url).run();

    }
}
