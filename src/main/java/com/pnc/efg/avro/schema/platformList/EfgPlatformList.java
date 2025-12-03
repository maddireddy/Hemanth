package com.pnc.efg.avro.schema.platformList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EfgPlatformList implements Serializable {
    private String publishDateTime;
    private String sourceSystem;
    private List<ProductRule> platformList;

    public EfgPlatformList() {
        this.platformList = new ArrayList<>();
    }

    public EfgPlatformList(String publishDateTime, String sourceSystem, List<ProductRule> platformList) {
        this.publishDateTime = publishDateTime;
        this.sourceSystem = sourceSystem;
        this.platformList = platformList != null ? new ArrayList<>(platformList) : new ArrayList<>();
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

    public List<ProductRule> getPlatformList() {
        return platformList;
    }

    public void setPlatformList(List<ProductRule> platformList) {
        this.platformList = platformList != null ? new ArrayList<>(platformList) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EfgPlatformList that = (EfgPlatformList) o;
        return Objects.equals(publishDateTime, that.publishDateTime) && Objects.equals(sourceSystem, that.sourceSystem) && Objects.equals(platformList, that.platformList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publishDateTime, sourceSystem, platformList);
    }

    @Override
    public String toString() {
        return "EfgPlatformList{" +
                "publishDateTime='" + publishDateTime + '\'' +
                ", sourceSystem='" + sourceSystem + '\'' +
                ", platformList=" + platformList +
                '}';
    }
}
