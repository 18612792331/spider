package com.gaot.spider.domin;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @Field("video_update_descript")
    private String videoUpdateDescript;

    private List<String> scriptwriter; // 编剧

    private List<String> actor; // 主演

    private List<String> genre; // 类型

    private List<String> area; // 地区

    private List<String> language;

    @Field("release_date_str")
    private String releaseDateStr; // 上映时间

    private String time; // 片场

    private String alias; // 别名

    private Grade douban;

    private Grade imdb;

    private Grade mtime;

    private Map<String, String> lfq;

    private String introduce;

    public MediaData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public List<String> getScriptwriter() {
        return scriptwriter;
    }

    public void setScriptwriter(List<String> scriptwriter) {
        this.scriptwriter = scriptwriter;
    }

    public List<String> getActor() {
        return actor;
    }

    public void setActor(List<String> actor) {
        this.actor = actor;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public String getReleaseDateStr() {
        return releaseDateStr;
    }

    public void setReleaseDateStr(String releaseDateStr) {
        this.releaseDateStr = releaseDateStr;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Grade getDouban() {
        return douban;
    }

    public void setDouban(Grade douban) {
        this.douban = douban;
    }

    public Grade getImdb() {
        return imdb;
    }

    public void setImdb(Grade imdb) {
        this.imdb = imdb;
    }

    public Grade getMtime() {
        return mtime;
    }

    public void setMtime(Grade mtime) {
        this.mtime = mtime;
    }

    public Map<String, String> getLfq() {
        return lfq;
    }

    public void setLfq(Map<String, String> lfq) {
        this.lfq = lfq;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getVideoUpdateDescript() {
        return videoUpdateDescript;
    }

    public void setVideoUpdateDescript(String videoUpdateDescript) {
        this.videoUpdateDescript = videoUpdateDescript;
    }

    @Override
    public String toString() {
        return "MediaData{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", videoUpdateDescript='" + videoUpdateDescript + '\'' +
                ", scriptwriter=" + scriptwriter +
                ", actor=" + actor +
                ", genre=" + genre +
                ", area=" + area +
                ", language=" + language +
                ", releaseDateStr='" + releaseDateStr + '\'' +
                ", time='" + time + '\'' +
                ", alias='" + alias + '\'' +
                ", douban=" + douban +
                ", imdb=" + imdb +
                ", mtime=" + mtime +
                ", lfq=" + lfq +
                ", introduce='" + introduce + '\'' +
                '}';
    }
}
