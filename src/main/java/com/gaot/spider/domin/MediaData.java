package com.gaot.spider.domin;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * @program: spider
 * @description:
 * @author: Mr.Gaot
 * @create: 2020-08-24 17:54
 **/
@Document("data_media")
public class MediaData {

    @Id
    private String id;

    private Integer type; //电影 1，剧集 2，动漫 3

    private String name;

    private Integer year;

    private List<String> scriptwriter; // 编剧

    private List<String> actor; // 主演

    private List<String> genre; // 类型

    private List<String> area; // 地区

    private List<String> language;

    private String releaseDateStr; // 上映时间

    private String time; // 片场

    private String alias; // 别名

    private List<Grade> douban;

    private List<Grade> imdb;

    private List<Grade> mtime;

    private Map<String, String> lfq;

    private String introduce;






}
