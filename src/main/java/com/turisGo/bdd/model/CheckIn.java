package com.turisGo.bdd.model;

import java.time.LocalDateTime;

public class CheckIn {
    private Integer id;
    private String geolocation;
    private LocalDateTime dateTime;
    private Integer touristId;
    private Integer attractionId;
    private Integer validatorInstituionId;

    public CheckIn() {

    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGeolocation() {
        return this.geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getTouristId() {
        return this.touristId;
    }

    public void setTouristId(Integer touristId) {
        this.touristId = touristId;
    }

    public Integer getAttractionId() {
        return this.attractionId;
    }

    public void setAttractionId(Integer attractionId) {
        this.attractionId = attractionId;
    }

    public Integer getValidatorInstituionId() {
        return this.validatorInstituionId;
    }

    public void setValidatorInstituionId(Integer validatorInstituionId) {
        this.validatorInstituionId = validatorInstituionId;
    }
}
