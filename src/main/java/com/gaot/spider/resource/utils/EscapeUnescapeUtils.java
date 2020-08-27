package com.gaot.spider.resource.utils;

public class EscapeUnescapeUtils {

    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * @disc 对字符串重新编码
     * @param src
     * @return
     */
    public static String isoToGB(String src) {
        String strRet = null;
        try {
            strRet = new String(src.getBytes("ISO_8859_1"), "GB2312");
        } catch (Exception e) {

        }
        return strRet;
    }

    /**
     * @disc 对字符串重新编码
     * @param src
     * @return
     */
    public static String isoToUTF(String src) {
        String strRet = null;
        try {
            strRet = new String(src.getBytes("ISO_8859_1"), "UTF-8");
        } catch (Exception e) {

        }
        return strRet;
    }

    public static void main(String[] args) {
        System.out.println(unescape("HC%24https%3A%2F%2Fv4.szjal.cn%2F20200820%2Fwlh56PKg%2Findex.m3u8%24%24%24HD%24https%3A%2F%2Ffangao.qfxmj.com%2Fconcat%2F20200823%2Fd5743a775c0b43f6bbb5c7eae8ea414c%2Fcloudv-transfer%2F5555555561rqq82q55569265os3711n6_e5b880e0c3074fa4a66b5577f11dc518_0_3.m3u8"));
    }
}
