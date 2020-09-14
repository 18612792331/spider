package com.gaot.spider.dto;

import java.util.List;

public class ConditionDTO {

    private List<AreaDTO> areaDTOList;

    public ConditionDTO() {
    }

    public List<AreaDTO> getAreaDTOList() {
        return areaDTOList;
    }

    public void setAreaDTOList(List<AreaDTO> areaDTOList) {
        this.areaDTOList = areaDTOList;
    }
}
