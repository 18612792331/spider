package com.gaot.spider.processor;

import com.gaot.spider.domin.Grade;
import com.gaot.spider.domin.MediaData;
import com.gaot.spider.resource.utils.HtmlunitUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PiankuProcessor2 implements PageProcessor {

    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    private Integer i = 1;

    private String baseUrl = "https://www.pianku.tv";

    private String resourceUrl = "https://www.pianku.tv/ajax/downurl/";

    private Site site = Site.me().setRetryTimes(5).setSleepTime(1000).setTimeOut(10000)
            .addCookie("Hm_lvt_5a21a69d1b034aed24dcda25771e8135", "1597749684")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
    @Override
    public void process(Page page) {
        if (page.getUrl().regex("https://www\\.pianku\\.tv/mv/------\\d+\\.html").match()) {
            System.out.println("第一层 ：" + page.getUrl().toString());
            dyList(page);

        } else if (page.getUrl().regex("https://www\\.pianku\\.tv/mv/(.{10})\\.html").match()){
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
        List<String> all = page.getHtml().xpath("//div[@class='li-img cover']/a/@href").all();
        all.forEach(data->{
            String url = baseUrl + data;
//            page.addTargetRequest(new Request(url).setPriority(1));
            try {
                String response = HtmlunitUtils.getHtmlPageResponse(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void dyDetail(Page page) {

        MediaData mediaData = new MediaData();
        System.out.println("----------------------------------" + i + "--------------------------------------------");
        String name = page.getHtml().xpath("//h1/text()").toString();
        if (StringUtils.isNotBlank(name)) mediaData.setName(name); // 片名
        String year = page.getHtml().xpath("//h1/span/text()").toString();
        if (StringUtils.isNotBlank(year)) mediaData.setYear(Integer.valueOf(year.replaceAll("\\(", "").replaceAll("\\)", "")));
        String videoUpdateDescript = page.getHtml().xpath("//div[@class='otherbox']/tidyText()").toString();
        if (StringUtils.isNotBlank(videoUpdateDescript)) mediaData.setVideoUpdateDescript(videoUpdateDescript);

        List<String> scriptwriters = page.getHtml().xpath("//div[@class='main-ui-meta']/div[3]//a/text()").all();
        if (scriptwriters.size()>0) mediaData.setScriptwriter(scriptwriters);  // 编剧
        List<String> actor = page.getHtml().xpath("//div[@class='main-ui-meta']/div[4]//a/text()").all();
        if (actor.size() > 0) mediaData.setActor(actor); // 主演
        List<String> genre = page.getHtml().xpath("//div[@class='main-ui-meta']/div[5]//a/text()").all();
        if (genre.size() > 0) mediaData.setGenre(genre); // 类型
        List<String> area = page.getHtml().xpath("//div[@class='main-ui-meta']/div[6]//a/text()").all();
        if (area.size()>0) page.putField("area", area); // 地区
        List<String> language = page.getHtml().xpath("//div[@class='main-ui-meta']/div[7]//a/text()").all();
        if (language.size()>0) mediaData.setLanguage(language); // 语言
        String releaseDateStr = page.getHtml().xpath("//div[@class='main-ui-meta']/div[8]/text()").toString();
        if (StringUtils.isNotBlank(releaseDateStr)) mediaData.setReleaseDateStr(releaseDateStr); // 上映时间
        String time = page.getHtml().xpath("//div[@class='main-ui-meta']/div[9]/text()").toString();
        if (StringUtils.isNotBlank(time)) mediaData.setTime(time); // 片长
        String alias = page.getHtml().xpath("//div[@class='main-ui-meta']/div[10]/text()").toString();
        if (StringUtils.isNotBlank(alias)) mediaData.setAlias(alias); //别名

        String douban = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='douban0']/a/text()").toString();
        if (StringUtils.isNotBlank(douban)) { // 豆瓣评分
            Double grade = 0.0;
            if (StringUtils.isNotBlank(douban.split(" ")[1]) && !douban.split(" ")[1].contains("N/A")) {
                grade = Double.valueOf(douban.split(" ")[1]);
            }
            String link = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='douban0']/a/@href").toString();
            Grade entity = new Grade();
            entity.setGrade(grade);
            entity.setLink(link);
            mediaData.setDouban(entity);
        } else {
            Grade entity = new Grade();
            entity.setGrade(0.0);
            mediaData.setDouban(entity);
        }
        String imdb = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='imdb0']/a/text()").toString();
        if (StringUtils.isNotBlank(imdb)) {
            Double grade = 0.0;
            if (StringUtils.isNotBlank(imdb.split(" ")[1]) && !imdb.split(" ")[1].contains("N/A")) {
                grade = Double.valueOf(imdb.split(" ")[1]);
            }
            String link = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='imdb0']/a/@href").toString();
            Grade entity = new Grade();
            entity.setGrade(grade);
            entity.setLink(link);
            mediaData.setImdb(entity);
        }
        String mtime = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='mtime0']/a/text()").toString();
        if (StringUtils.isNotBlank(mtime)) {
            Double grade = 0.0;
            if (StringUtils.isNotBlank(mtime.split(" ")[1]) && !mtime.split(" ")[1].contains("N/A")) {
                grade = Double.valueOf(mtime.split(" ")[1]);
            }
            String link = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='mtime']/a/@href").toString();
            Grade entity = new Grade();
            entity.setGrade(grade);
            if (StringUtils.isNotBlank(link)) entity.setLink(link);
            mediaData.setMtime(entity);
        }

        String lfq0 = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='lfq0']/a/text()").toString();
        if (StringUtils.isNotBlank(lfq0)) {
            String grade = lfq0.split(" ")[1];
            String link = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='lfq0']/a/@href").toString();
            Map<String, String> map = new HashMap<>();
            map.put("grade", grade);
            map.put("link", link);
            mediaData.setLfq(map);
        }
        String introduce = page.getHtml().xpath("//p[@class='sqjj_a']/text()").toString();
        if (StringUtils.isNotBlank(introduce)) mediaData.setIntroduce(introduce);
        mongoTemplate.save(mediaData);
        System.out.println("id===============================" + mediaData.getId());
        String currentUrl = page.getUrl().toString();
        String suffix = currentUrl.substring(currentUrl.lastIndexOf("/")+1, currentUrl.lastIndexOf(".")) + "_mv/";

        String rPath = resourceUrl + suffix;
        page.addTargetRequest(new Request(rPath).setPriority(2).putExtra("model", mediaData));

        i++;
    }
}