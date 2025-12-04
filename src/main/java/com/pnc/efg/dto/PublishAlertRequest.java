package com.pnc.efg.dto;

import java.util.List;

public class PublishAlertRequest {
    private String publishDateTime;
    private String sourceSystem;
    private List<AlertDto> alerts;

    public PublishAlertRequest() { }

    public PublishAlertRequest(String publishDateTime, String sourceSystem, List<AlertDto> alerts) {
        this.publishDateTime = publishDateTime;
        this.sourceSystem = sourceSystem;
        this.alerts = alerts;
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

    public List<AlertDto> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertDto> alerts) {
        this.alerts = alerts;
    }
}
