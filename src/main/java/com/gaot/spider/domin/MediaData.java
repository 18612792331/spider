package com.gaot.spider.domin;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

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

    private Integer type; //电影 1，剧集 2，综艺 3, 动漫 4

    private String name;

    private String cover;

    private Integer year;

    private String state;

    private String director; // 导演

    private List<String> actor; // 主演

    private String genre; // 类型

    private String area; // 地区

    private String language;

    private String alias; // 别名

    private String introduce;

    private List<MediaDataResource> resources;

    public MediaData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<String> getActor() {
        return actor;
    }

    public void setActor(List<String> actor) {
        this.actor = actor;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public List<MediaDataResource> getResources() {
        return resources;
    }

    public void setResources(List<MediaDataResource> resources) {
        this.resources = resources;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "MediaData{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", actor=" + actor +
                ", genre=" + genre +
                ", area=" + area +
                ", language=" + language +
                ", alias='" + alias + '\'' +
                ", introduce='" + introduce + '\'' +
                '}';
    }
}
