package com.gaot.spider.dto;

import java.util.List;

public class AreaDTO {

    private String title;

    private String condition;

    public AreaDTO() {
    }

    public AreaDTO(String title, String condition) {
        this.title = title;
        this.condition = condition;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
