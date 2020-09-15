package com.gaot.spider.dto;

import java.util.List;

public class ConditionDTO {

    private List<AreaDTO> areaDTOList;
    private List<String> genreList;

    public ConditionDTO() {
    }

    public List<AreaDTO> getAreaDTOList() {
        return areaDTOList;
    }

    public void setAreaDTOList(List<AreaDTO> areaDTOList) {
        this.areaDTOList = areaDTOList;
    }

    public List<String> getGenreList() {
        return genreList;
    }

    public void setGenreList(List<String> genreList) {
        this.genreList = genreList;
    }
}
