package com.rahul.newsdroid.Data;

public class DataScore {

    public String scoreImgLeft, scoreImgRight, scoreTime, scoreDate;
    public int scoreTeamLeft, scoreTeamRight;

    public DataScore(String scoreImgLeft, String scoreImgRight, String scoreTime, String scoreDate, int scoreTeamLeft, int scoreTeamRight) {
        this.scoreImgLeft = scoreImgLeft;
        this.scoreImgRight = scoreImgRight;
        this.scoreTime = scoreTime;
        this.scoreDate = scoreDate;
        this.scoreTeamLeft = scoreTeamLeft;
        this.scoreTeamRight = scoreTeamRight;
    }

    public String getScoreImgLeft() {
        return scoreImgLeft;
    }

    public void setScoreImgLeft(String scoreImgLeft) {
        this.scoreImgLeft = scoreImgLeft;
    }

    public String getScoreImgRight() {
        return scoreImgRight;
    }

    public void setScoreImgRight(String scoreImgRight) {
        this.scoreImgRight = scoreImgRight;
    }

    public String getScoreTime() {
        return scoreTime;
    }

    public void setScoreTime(String scoreTime) {
        this.scoreTime = scoreTime;
    }

    public String getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(String scoreDate) {
        this.scoreDate = scoreDate;
    }

    public int getScoreTeamLeft() {
        return scoreTeamLeft;
    }

    public void setScoreTeamLeft(int scoreTeamLeft) {
        this.scoreTeamLeft = scoreTeamLeft;
    }

    public int getScoreTeamRight() {
        return scoreTeamRight;
    }

    public void setScoreTeamRight(int scoreTeamRight) {
        this.scoreTeamRight = scoreTeamRight;
    }
}
