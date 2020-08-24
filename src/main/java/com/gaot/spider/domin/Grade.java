package com.gaot.spider.domin;

public class Grade {

    private Double grade;

    private String link;

    public Grade() {
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "grade=" + grade +
                ", link='" + link + '\'' +
                '}';
    }
}
