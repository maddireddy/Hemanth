package com.pnc.efg.dto;

public class AlertDto {
    private String alertId;
    private String eventTime;
    private String alertType;
    private String severity;
    private String message;

    public AlertDto() {
    }

    public AlertDto(String alertId, String eventTime, String alertType, String severity, String message) {
        this.alertId = alertId;
        this.eventTime = eventTime;
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
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
}
