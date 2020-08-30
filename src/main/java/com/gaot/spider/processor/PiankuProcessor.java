package com.gaot.spider.processor;

import com.gaot.spider.domin.MediaData;
import com.gaot.spider.domin.MediaDataResource;
import com.gaot.spider.domin.MediaDataResourceLink;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PiankuProcessor implements PageProcessor {

    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    private Integer i = 1;

    private String baseUrl = "https://www.pianku.tv";

    private String resourceUrl = "https://www.pianku.tv/ajax/downurl/";

    private final String DYCODE = "mv";

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
        } else if (page.getUrl().regex("https://www\\.pianku\\.tv/ajax/downurl/(.{10})_mv/").match()) {
            System.out.println("视频资源======================");
            resourceList(page);
        }




    }
    Document doc = null;

    // 在线和下载资源
    public void resourceList(Page page){
        MediaData mediaData = (MediaData) page.getRequest().getExtra("model");
        System.out.println("===========================================================================================================");
        System.out.println(mediaData.toString());
        List<String> titles = page.getHtml().xpath("//ul[@class='py-tabs']//li/text()").all();
        List<Selectable> nodes = page.getHtml().xpath("//div[@class='bd']//ul").nodes();
        // 保存播放链接
        List<MediaDataResource> resources = new ArrayList<>();
        for (int j = 0; j < titles.size() ; j++) {
            MediaDataResource dataResource = new MediaDataResource();
            String title = titles.get(j);
            dataResource.setLabel(title);
            System.out.println(title);
            List<MediaDataResourceLink> resourceLinks = new ArrayList<>();
            nodes.forEach(node->{
                MediaDataResourceLink resourceLink = new MediaDataResourceLink();
                String linkTitle = node.xpath("//ul//li//a/text()").toString();
                System.out.println(linkTitle);
                resourceLink.setTitle(linkTitle);
                String uri = node.xpath("//ul//li//a/@href").toString();
                System.out.println(uri);
                try {
                    System.out.println(baseUrl + uri);

                    doc = Jsoup.connect(baseUrl+uri).validateTLSCertificates(true).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36").timeout(20000).get();
                    Pattern p= Pattern.compile("http.+\\.m3u8");
                    String link = null;
                    Matcher m=p.matcher(doc.toString());
                    while(m.find()){

                        link =m.group();
                        System.out.println("link=  " + link);
                        return;
                    }
                    resourceLink.setLink(link);
                } catch (IOException e) {
                    System.out.println(mediaData.getName() + "==========出错");
                    e.printStackTrace();

                }
                resourceLinks.add(resourceLink);

            });

            resources.add(dataResource);

        }
        mediaData.setResources(resources);
        mongoTemplate.save(mediaData);
    }

    // 爬取电影列表页
    public void dyList(Page page) {
        List<String> all = page.getHtml().xpath("//div[@class='li-img cover']/a/@href").all();
        all.forEach(data->{
            String url = baseUrl + data;
            page.addTargetRequest(new Request(url).setPriority(1));
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

        } else {

        }
        String imdb = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='imdb0']/a/text()").toString();
        if (StringUtils.isNotBlank(imdb)) {
            Double grade = 0.0;
            if (StringUtils.isNotBlank(imdb.split(" ")[1]) && !imdb.split(" ")[1].contains("N/A")) {
                grade = Double.valueOf(imdb.split(" ")[1]);
            }
            String link = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='imdb0']/a/@href").toString();

        }
        String mtime = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='mtime0']/a/text()").toString();
        if (StringUtils.isNotBlank(mtime)) {
            Double grade = 0.0;
            if (StringUtils.isNotBlank(mtime.split(" ")[1]) && !mtime.split(" ")[1].contains("N/A")) {
                grade = Double.valueOf(mtime.split(" ")[1]);
            }
            String link = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='mtime']/a/@href").toString();

        }

        String lfq0 = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='lfq0']/a/text()").toString();
        if (StringUtils.isNotBlank(lfq0)) {
            String grade = lfq0.split(" ")[1];
            String link = page.getHtml().xpath("//div[@class='main-ui-meta']/div[11]/div[@class='lfq0']/a/@href").toString();
            Map<String, String> map = new HashMap<>();
            map.put("grade", grade);
            map.put("link", link);

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

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String str="<script>\n" +
                "const dp = new DPlayer({\n" +
                "    container: document.getElementById('video'),\n" +
                "    screenshot: true,\n" +
                "\tpreload: 'metadata',\n" +
                "\tvolume: 1.0,\n" +
                "    video: {\n" +
                "        url: ''\n" +
                "    }\n" +
                "});\n" +
                "geturl('https://diaoshi.dehua-kuyun.com/20200826/13743_98d2481a/index.m3u8');\n" +
                "var pycount=9;\n" +
                "function adremove(){$('#playad').remove();dp.play();}\n" +
                "function jj(){\n" +
                "if (pycount == 0){adremove();}else if(pycount>0){\n" +
                "$('#pp>font').text(pycount);pycount -= 1;setTimeout(function () {jj(pycount);}, 1000);}\n" +
                "}\n" +
                "function geturl(url){if(url.indexOf('.m3u8')>=0){dp.switchVideo({url: url});}else{pyjs(url);}}\n" +
                "function pyjs(url){\n" +
                "$('#video').append('<div id=\"loading\" style=\"text-align:center;z-index:20;background-color:black;top: 0;position: absolute;display: table;width:100%;height:100%;font-size:16px;\"><div style=\"width:100%;height:100%;color:#999;display:table-cell;vertical-align: middle;\"><span class=\"tips\">正在加载中，请稍候...</div></div>');\n" +
                "$.ajax({type:\"post\",url:'/playjson/index.php',data:{'url':url},dataType:'json',timeout:10000,success:function(data){\n" +
                "if(data.code==200){\n" +
                "var typer='auto';if(data.type=='hls'||data.type=='m3u8'){typer='hls';}dp.switchVideo({url: data.url,type:typer});\n" +
                "}else{\n" +
                "$('.tips').text('加载失败，请刷新重试或切换播放源');\n" +
                "}},\n" +
                "complete : function(XMLHttpRequest,status){\n" +
                "if(status=='timeout'){\n" +
                "$('.tips').text('请求超时，请刷新重试或切换播放源');\n" +
                "}}});\n" +
                "}\n" +
                "dp.on('loadstart', function () {\n" +
                "$.getScript('/ajax/historys/lNWZ0MWbkNmN_'+page+'/');\n" +
                "$('#loading').remove();\n" +
                "$('#video').append('<div id=\"playad\" style=\"background-color:black;z-index:99;top:0;position:absolute;width:100%;height:100%;\"><div id=\"pp\" style=\"position:absolute;z-index:100;color:#fff;padding:20px;right:0;\">广告倒计时 <font style=\"color:#f90;\"></font> 秒<button onclick=\"adremove()\" style=\"margin-left:20px;\">关闭广告</button></div><a href=\"https://www.yabo816.com\" target=\"_blank\" style=\"background:url(https://pic.gksec.com/2020/08/18/cd1157b8f4fba/dp.jpg) center center no-repeat;background-size:100% auto;width:100%;height:100%;display:block;\"></a></div>');\n" +
                "jj();\n" +
                "});\n" +
                "var page=1;\n" +
                "var videotime=0;\n" +
                "dp.on('timeupdate', function () {\n" +
                "videotime++;\n" +
                "if(videotime==20){\n" +
                "$.getScript('/ajax/historys/lNWZ0MWbkNmN_'+page+'_time_'+Math.round(dp.video.currentTime)+'_'+Math.round(dp.video.currentTime/dp.video.duration*100)+'/');\n" +
                "}else if(videotime>150){\n" +
                "videotime=0;\n" +
                "}\n" +
                "});\n" +
                "setTimeout(function(){if(videotime==0){$.getScript('/ajax/historys/lNWZ0MWbkNmN_'+page+'_time_0_0/');}}, 5000);\n" +
                "$(\".box_con a\").click(function(){\n" +
                "window.location.replace($(this).attr('href'));\n" +
                "return false;\n" +
                "});\n" +
                "</script>\n" +
                "<footer>\n" +
                "<a class=\"to-top\"><span class=\"icon-chevron-thin-up\"></span></a>\n" +
                "<div class=\"footer1\">\n" +
                "本站所有资源均收集自互联网，没有提供影片资源存储，也未参与录制、上传。若本站收录的资源涉及您的版权或知识产权或其他利益，请附上版权证明邮件告知。<span class=\"right\"><a href=\"javascript:;\" onclick=\"xtip.alert('邮箱：pianku\uD83D\uDE03protonmail.com<br>注意：\uD83D\uDE03=@')\">获取邮箱</a> · PiANKU · 8.5ms</span>\n" +
                "</div>\n" +
                "</footer>\n" +
                "<!--[if lt IE 8]>\n" +
                "<script>window.location = '/browser.html';</script>\n" +
                "<![endif]-->\n" +
                "<script type=\"text/javascript\">\n" +
                "function check_webp(){return document.createElement('canvas').toDataURL('image/webp').indexOf('data:image/webp') == 0;}\n" +
                "FunLazy({\n" +
                "placeholder: '/static/css/l.gif',\n" +
                "beforeLazy: function(src){if(check_webp()){src=src.replace(\".jpg\",\".webp\");}return src;}\n" +
                "});\n" +
                "$('.to-top').toTop();$('header').scrollupbar();\n" +
                "</script>";
        Pattern p= Pattern.compile("http.+\\.m3u8");
        Matcher m=p.matcher(str);
        while(m.find()){
            System.out.println(m.group());
        }

    }
}
