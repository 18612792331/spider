package com.gaot.spider.processor;

import com.gaot.spider.domin.MediaData;
import com.gaot.spider.domin.MediaDataResource;
import com.gaot.spider.domin.MediaDataResourceLink;
import com.gaot.spider.resource.utils.JsoupUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmdyiProcessor implements PageProcessor {

    private Integer type; //电影 1，剧集 2，综艺 3, 动漫 4

    private Integer count;

    private MongoTemplate mongoTemplate;

    private String baseUrl = "http://www.smdyi.com";

    public void setType(Integer type) {
        this.type = type;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    private Site site = Site.me().setRetryTimes(5).setCycleRetryTimes(3).setSleepTime(500).setTimeOut(10000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
    @Override
    public void process(Page page) {
//        http://www.smdyi.com/search.php?searchtype=5&tid=2
        if (page.getUrl().regex("http://www\\.smdyi\\.com/search\\.php(.+)").match()) {
            System.out.println("第一层 ：" + page.getUrl().toString());
            dyList(page);
        //http://www.smdyi.com/dsj/tianlongbabuyueyu/
        } else if (page.getUrl().regex("http://www\\.smdyi\\.com/(dsj|dm|dy)(.+)").match()) {
            dyDetail(page);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void dyList(Page page) {
        List<String> all = page.getHtml().xpath("//li[@class='mb']//div[@class='text]//a/@href").all();
        Collections.reverse(all);
        for (String href:all) {
            page.addTargetRequest(new Request(baseUrl + href).setPriority(1));
        }
//        String url="http://www.smdyi.com/search.php?page=748&searchtype=5&tid=2";
        if (count >= 1) {
            page.addTargetRequest(new Request("http://www.smdyi.com/search.php?page=" + count + "&searchtype=5&tid=" + type).setPriority(3));
        }

        count--;
    }

    public void dyDetail(Page page) {
        System.out.println("=================================当前页数：" + (count+2) + "==================================");
        Document doc = null;
        MediaData mediaData = new MediaData();
        mediaData.setCreateTime(LocalDateTime.now());
        mediaData.setType(type);
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
        List<Selectable> nodes = page.getHtml().xpath("//div[@class='wrap']/div[@class='index-tj cxg clearfix']").nodes();
        if (nodes.size()>1 && StringUtils.isNotBlank(nodes.get(1).$("ul","text").toString())) mediaData.setIntroduce(nodes.get(1).$("ul","text").toString().trim());
        System.out.println("剧情：" + mediaData.getIntroduce());
        List<Selectable> playerList = null;
        if (type==2 || type==4) {
            playerList = page.getHtml().xpath("//div[@class='plist clearfix mainplist']").nodes();
        }
        if (type==1) {
            playerList = page.getHtml().xpath("//div[@class='plist clearfix']").nodes();
        }
        List<MediaDataResource> resources = new ArrayList<>();
        int lineIndex=1;
        for (Selectable xl: playerList) {
            MediaDataResource mediaDataResource = new MediaDataResource();
            mediaDataResource.setLabel("线路"+lineIndex);
            System.out.println("线路   "+lineIndex+"   ======================================");
            List<Selectable> uriList = xl.xpath("//ul[@class='urlli']//ul//li").nodes();

            List<MediaDataResourceLink> resourceLinkList = new ArrayList<>();
            for (Selectable link: uriList) {
                MediaDataResourceLink resourceLink = new MediaDataResourceLink();
                String uri = link.xpath("//a/@href").toString();
                String title = link.xpath("//a/text()").toString();
                resourceLink.setTitle(title);
                if (StringUtils.isNotBlank(uri) && !uri.contains("javascript:;")) {
                    uri = baseUrl+uri;
                    try {
                        doc = JsoupUtils.getDocument(uri);
                        if (StringUtils.isNotBlank(doc.html())) {
                            String player = doc.getElementsByClass("player").select("script").html();
                            if (StringUtils.isNotBlank(player)) {

                                String result = getLink(player);
                                if (StringUtils.isNotBlank(result)) {
                                    result = result.substring(0, result.indexOf(".m3u8")+5);
                                    resourceLink.setLink(result.trim());
                                    System.out.println(title + " 播放链接：" + resourceLink.getLink());

                                } else {
                                    System.out.println("没有匹配到");
                                    break;
                                }
                            }
                        }
                        System.out.println("uri:" + uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else { break; }

                resourceLinkList.add(resourceLink);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (resourceLinkList.size()>0) {
                mediaDataResource.setLinks(resourceLinkList);
                resources.add(mediaDataResource);
                lineIndex++;
            }
        }

        mediaData.setResources(resources);
        if (resources.size()>0) {
            mongoTemplate.save(mediaData);
        }


    }

    public String getLink(String text) {
        Pattern pattern = Pattern.compile("http(.+)m3u8");
        Matcher matcher = pattern.matcher(text);
        String result = null;
        if (matcher.find()) {
            result = matcher.group();

        }
        return result;
    }

    public static void main(String[] args) {
        String url = "http://www.smdyi.com/search.php?page=2&searchtype=5&tid=2";
        Spider.create(new SmdyiProcessor()).addUrl(url).run();

    }
}
