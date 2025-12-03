package com.pnc.efg.dto;

import java.util.List;

public class PublishRequest {
    private String publishDateTime;
    private String sourceSystem;
    private List<ItemDto> items;

    public PublishRequest() { }

    public PublishRequest(String publishDateTime, String sourceSystem, List<ItemDto> items) {
        this.publishDateTime = publishDateTime;
        this.sourceSystem = sourceSystem;
        this.items = items;
    }

    public String getPublishDateTime() {
        return publishDateTime;
    }

    public void setPublishDateTime(String publishDateTime) {
        this.publishDateTime = publishDateTime;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public List<ItemDto> getItems() {
        return items;
    }

    public void setItems(List<ItemDto> items) {
        this.items = items;
    }
}
