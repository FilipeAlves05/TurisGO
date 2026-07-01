package com.turisGo.bdd.dto;

import com.turisGo.bdd.model.CheckIn;

public class CheckInResult {
    private CheckIn checkIn;
    private int pointsAwarded;
    private int totalPoints;
    private int level;

    public CheckInResult(CheckIn checkIn, int pointsAwarded, int totalPoints, int level) {
        this.checkIn = checkIn;
        this.pointsAwarded = pointsAwarded;
        this.totalPoints = totalPoints;
        this.level = level;
    }

    public CheckIn getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(CheckIn checkIn) {
        this.checkIn = checkIn;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
