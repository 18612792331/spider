package com.gaot.spider.resource.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @program: spider
 * @description:
 * @author: Mr.Gaot
 * @create: 2020-09-02 12:39
 **/
public class JsoupUtils {
    public static void main(String[] args) {
        System.out.println(getrandom(300, 600));
    }

    public static int getrandom(int start,int end) {

        int num=(int) (Math.random()*(end-start+1)+start);
        return num;
    }

    public static List<Map<String, Object>> ipPools;
    private static int currentIndex = 0;

    public static Document getDocument(String url) throws Exception {
        Document doc = null;
        StringWriter strWriter = new StringWriter();
        PrintWriter prtWriter = new PrintWriter(strWriter);

        // En:get max retry count from properties file(com-constants.properties)
        // 通过properties获取最大retry次数
        /*int maxRetry = Integer.parseInt(PropertyReader.getProperties(SystemConstants.COM_CONSTANTS)
                .getProperty(UtilsConstants.MAX_RETRY_COUNT));*/
        int maxRetry = 3;
        // En: get sleep time from properties file Jp:プロパティファイルでロックタイムアウトのスリープ時間を取得する
        /*int sleepTime = Integer.parseInt(PropertyReader.getProperties(SystemConstants.COM_CONSTANTS)
                .getProperty(UtilsConstants.SLEEP_TIME_COUNT));*/

        int sleepTime = 500;
        // En: if exception is occurred then retry loop is continue to run;
        // Jp: 異常を起きる場合、ループを続き実行する。
        for (int j = 1; j <= maxRetry; j++) {

            try {
                if (j != 1) {
//                    ipPools.remove(currentIndex);
                    Thread.sleep(getrandom(200, 400));
                }
                Random random = new Random();
                int i = random.nextInt(ipPools.size());
                currentIndex = i;
                doc = Jsoup.connect(url).timeout(10 * 1000)
                        .proxy(String.valueOf(ipPools.get(i).get("ip")), (int) ipPools.get(i).get("port"))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36")
                        .ignoreContentType(true).ignoreHttpErrors(true).validateTLSCertificates(false)
                        .get();

                // En: normal finish situation,loop is broken.
                // Jp: サービスが正常に終了した場合、ループを中止します。
                // Zh: 正常终了的情况、终止循环。
                break;

            } catch (Exception ex) {
                // throw new Exception(ex); dead code is occurred

                // StackTraceを文字列で取得
                ex.printStackTrace(prtWriter);
                String stackTrace = strWriter.toString();

                if (strWriter != null) {
                    try {
                        strWriter.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                if (prtWriter != null) {
                    prtWriter.close();
                }
                System.out.println(stackTrace);
            }
        }
        return doc;
    }
}
