package com.gaot.spider.resource;

import com.gaot.spider.resource.utils.HtmlunitUtils;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexResource {



    public static void main(String[] args) throws Exception {
        String url = "https:\\/\\/s3.135-cdn.com\\/2020\\/08\\/27\\/lqTNbA720caxBXGi\\/index.m3u8";
        String regex = "http(.+)m3u8";
//        String regex = "https://www.subofm.com/index.php/vod/show/id/1.html";

        System.out.println(url.matches(regex));
    }
}
