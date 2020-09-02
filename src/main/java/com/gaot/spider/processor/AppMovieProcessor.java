package com.gaot.spider.processor;

import com.alibaba.fastjson.JSONObject;
import com.gaot.spider.domin.MediaData;
import com.gaot.spider.domin.MediaDataResource;
import com.gaot.spider.domin.MediaDataResourceLink;
import com.gaot.spider.resource.utils.JsoupUtils;
import com.gaot.spider.resource.utils.SslUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.io.IOException;
import java.util.*;

/**
 * @program: spider
 * @description:
 * @author: Mr.Gaot
 * @create: 2020-08-28 11:13
 **/

@Component
public class AppMovieProcessor implements PageProcessor {

    private Integer type; //电影 1，剧集 2，综艺 3, 动漫 4

    private Integer count;

    private MongoTemplate mongoTemplate;

    private List<Map<String, Object>> ipPools;

    public void setIpPools(List<Map<String, Object>> ipPools) {
        this.ipPools = ipPools;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    private Integer i = 2;

    private String baseUrl = "https://app.movie";
    private Site site = Site.me().setRetryTimes(5).setCycleRetryTimes(3).setSleepTime(500).setTimeOut(10000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");

    @Override
    public void process(Page page) {
        if (page.getUrl().regex("https://app\\.movie/index\\.php/vod/type/id/(.+)html").match()) {
            System.out.println("第一层 ：" + page.getUrl().toString());
            dyList(page);

        } else if (page.getUrl().regex("https://app\\.movie/index\\.php/vod/detail/(.+)html").match()) {
            dyDetail(page);
        } else if (page.getUrl().regex("https://app\\.movie/index\\.php/vod/play/(.+)html").match()) {
            dyPlay(page);
        }
    }

    public void dyPlay(Page page) {
        MediaData mediaData = (MediaData) page.getRequest().getExtra("model");

        List<Selectable> nodes = page.getHtml().xpath("//div[@class='content']//section").nodes();
        List<MediaDataResource> resources = new ArrayList<>();
        System.out.println("电影名：" + mediaData.getName());
//        Document doc = null;
        for (Selectable node:nodes) {
            MediaDataResource resource = new MediaDataResource();
            String label = node.xpath("//h2/text()").toString();
            System.out.println("label: " + label);
            resource.setLabel(label);
            List<Selectable> selectables = node.xpath("//ul//li").nodes();
            List<MediaDataResourceLink> resourceLinks = new ArrayList<>();
            for (Selectable li: selectables) {
                MediaDataResourceLink resourceLink = new MediaDataResourceLink();
                String linkTitle = li.xpath("//li//a/text()").toString();
                resourceLink.setTitle(linkTitle);
                String href = li.xpath("//li//a/@href").toString();
                System.out.println("标题： " + linkTitle);
                try {
                    Random random = new Random();
                    int i = random.nextInt(10);
                    Document doc = JsoupUtils.getDocument(baseUrl + href);
                    if (null != doc) {
                        String first = doc.getElementsByClass("stui-player__video embed-responsive embed-responsive-16by9 clearfix").select("script").first().html();
                        if (StringUtils.isNotBlank(first)) {
                            first = first.trim().replaceAll("var player_data=", "");
                            JSONObject jsonObject = JSONObject.parseObject(first);
                            String url = jsonObject.getString("url");
                            resourceLink.setLink(url.trim());
                            System.out.println(".m3u8 url :  " + resourceLink.getLink());
                        }
                        Thread.sleep(JsoupUtils.getrandom(300, 500));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                resourceLinks.add(resourceLink);
            }

            resource.setLinks(resourceLinks);
            resources.add(resource);
        }

        mediaData.setResources(resources);

        mongoTemplate.save(mediaData);
    }

    public void dyDetail(Page page) {
        MediaData mediaData = new MediaData();
        mediaData.setType(2);
        String name = page.getHtml().xpath("//div[@class='stui-content__detail fl-l']/h3/text()").toString();
        if (StringUtils.isNotBlank(name)) mediaData.setName(name);
        System.out.println("名称：" + name);
        String cover = page.getHtml().xpath("//img[@class='img-responsive lazyload']/@data-original").toString();
        if (StringUtils.isNotBlank(cover)) mediaData.setCover(cover);
        System.out.println("图片：" + cover);

        String genre = page.getHtml().xpath("//div[@class='stui-content__detail fl-l']/p[1]/a[1]/text()").toString();
        System.out.println("类型：" + genre);
        if (StringUtils.isNotBlank(genre)) mediaData.setGenre(genre);
        String area = page.getHtml().xpath("//div[@class='stui-content__detail fl-l']/p[1]/a[2]/text()").toString();
        System.out.println("地区：" + area);
        if (StringUtils.isNotBlank(area)) mediaData.setArea(area);
        String year = page.getHtml().xpath("//div[@class='stui-content__detail fl-l']/p[1]/a[3]/text()").toString();

        if (StringUtils.isNotBlank(year.trim()) && year.trim().matches("\\d+"))
            mediaData.setYear(Integer.valueOf(year));
        System.out.println("年份：" + mediaData.getYear());
        String state = page.getHtml().xpath("//div[@class='stui-content__detail fl-l']/p[2]/text()").toString();
        if (StringUtils.isNotBlank(state)) mediaData.setState(state.trim());
        System.out.println("状态：" + mediaData.getState());
        String actor = page.getHtml().xpath("//div[@class='stui-content__detail fl-l']/p[3]/text()").toString();
        if (StringUtils.isNotBlank(actor)) mediaData.setActor(actor.split(","));
        System.out.println("主演：" + actor);
        String director = page.getHtml().xpath("//div[@class='stui-content__detail fl-l']/p[4]/text()").toString();
        if (StringUtils.isNotBlank(director)) mediaData.setDirector(director);
        System.out.println("导演：" + mediaData.getDirector());
        String introduce = page.getHtml().xpath("//div[@class='stui-content__desc col-pd clearfix']/text()").toString();
        if (StringUtils.isNotBlank(introduce)) mediaData.setIntroduce(introduce.trim());
        System.out.println("简介：" + mediaData.getIntroduce());
        mongoTemplate.save(mediaData);
        System.out.println("id: " + mediaData.getId());


        String uri = page.getHtml().xpath("//div[@class='playbtn']/a/@href").toString();
        System.out.println("uri=" + uri);
        page.addTargetRequest(new Request(baseUrl + uri).setPriority(1).putExtra("model", mediaData));


    }

    // 爬取电影列表页
    public void dyList(Page page) {

        List<String> all = page.getHtml().xpath("//a[@class='stui-vodlist__thumb lazyload']/@href").all();
        Collections.reverse(all);
        /*Integer index = 1;
        for (String data:all) {
            if (index<=3) {
                String url = baseUrl + data;
                page.addTargetRequest(new Request(url).setPriority(2));
            }
            index++;
        }*/
        for (String data : all) {
            String url = baseUrl + data;
            page.addTargetRequest(new Request(url).setPriority(1));
        }
//        String url="https://app.movie/index.php/vod/type/id/1/page/1.html";
        if (count >= 630) {
            page.addTargetRequest(new Request("https://app.movie/index.php/vod/type/id/" + type + "/page/" + count + ".html").setPriority(3));
        }

        count--;


    }

    @Override
    public Site getSite() {
        return site;
    }

}
