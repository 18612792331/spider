package com.gaot.spider.resource;

import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.PlainText;

public class IndexResource {

    public static void main(String[] args) throws Exception {
        String url = "https://www.pianku.tv/mv/wNnFGOjBza.html";
        /*String pattern = "https://www\\.pianku\\.tv/mv/(.{10})\\.html";
        boolean isMatch = Pattern.matches(pattern, url);
        System.out.println(isMatch);*/
        long start = System.currentTimeMillis();
        /*String result = getAjaxContent("https://www.pianku.tv/mv/wNnFGOjBza.html");
        System.out.println(result);
        // 创建新文件
        String path = "D:\\Download\\test.html";
        PrintWriter printWriter = null;
        printWriter = new PrintWriter(new FileWriter(new File(path)));
        printWriter.write(result);
        printWriter.close();*/

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
//ssl证书支持
        desiredCapabilities.setCapability("acceptSslCerts", true);
//截屏支持，这里不需要
        desiredCapabilities.setCapability("takesScreenshot", false);
//css搜索支持
        desiredCapabilities.setCapability("cssSelectorsEnabled", true);
//js支持
        desiredCapabilities.setJavascriptEnabled(true);
//驱动支持
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "D:/software/install/phantomjs-2.1.1-windows/bin/phantomjs.exe");
//创建无界面浏览器对象
        PhantomJSDriver driver = new PhantomJSDriver(desiredCapabilities);
//这里注意，把窗口的大小调整为最大，如果不设置可能会出现元素不可用的问题
        driver.manage().window().maximize();
        driver.get(url);

        WebElement element = driver.findElementByClassName("down-list");
        System.out.println(element.toString());
        long end = System.currentTimeMillis();
        System.out.println("===============耗时：" + (end - start)
                + "===============");

    }

    public static String getAjaxContent(String url) throws Exception {
        Runtime rt = Runtime.getRuntime();
        Process p = rt
                .exec("D:/software/install/phantomjs-2.1.1-windows/bin/phantomjs.exe D:/s.js "
                        + url);
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sbf = new StringBuffer();
        String tmp = "";
        while ((tmp = br.readLine()) != null) {
            sbf.append(tmp + "\n");
        }
        return sbf.toString();
    }

    public static Page download(Request request) {
        Page page = new Page();
        try {
            String url = request.getUrl();
            String html = getAjaxContent(url);
            page.setRawText(html);
            page.setUrl(new PlainText(url));
            page.setRequest(request);
            return page;
        } catch (Exception e) {
            System.out.println("download出错了!");
            return page;
        }
    }
}
