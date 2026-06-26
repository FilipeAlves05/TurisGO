package com.turisGo.bdd.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Tourist extends User {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private Integer totalPoints;
    private Integer level;

    public Tourist() {
        super();
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getTotalPoints() {
        return this.totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

}
