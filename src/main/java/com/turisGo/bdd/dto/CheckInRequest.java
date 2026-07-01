package com.turisGo.bdd.dto;

public class CheckInRequest {
    private Integer touristId;
    private Integer attractionId;
    private String geolocation;
    private Integer amount;

    public CheckInRequest() {

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

    public String getGeolocation() {
        return this.geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
