package com.gaot.spider.domin;

import java.util.List;

public class MediaDataResource {

    private String label;

    private List<MediaDataResourceLink> links;

    public MediaDataResource() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<MediaDataResourceLink> getLinks() {
        return links;
    }

    public void setLinks(List<MediaDataResourceLink> links) {
        this.links = links;
    }
}
