// Caminho: bdd/model/Review.java
package com.turisGo.bdd.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Review {
    private Integer id;
    private Integer touristId;
    private Integer attractionId;
    private String comment;
    private Integer rating;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reviewDate;

    private String imageUrl;

    public Review() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTouristId() {
        return touristId;
    }

    public void setTouristId(Integer touristId) {
        this.touristId = touristId;
    }

    public Integer getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(Integer attractionId) {
        this.attractionId = attractionId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}