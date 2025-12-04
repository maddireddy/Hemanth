package com.pnc.efg.avro.schema.alerts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple POJO for alerts used with Avro reflect serializer.
 */
public class EfgAlert implements Serializable {
    private String publishDateTime;
    private String sourceSystem;
    private String alertId;
    private String eventTime;
    private String alertType;
    private String severity;
    private String message;
    private Map<String, String> details;

    public EfgAlert() {
        this.details = new HashMap<>();
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

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
}
