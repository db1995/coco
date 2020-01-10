package com.github.coco.vo;

/**
 * @author db1995
 */
public class StatisticsVO {
    private int todayServed;
    private int todayOnline;
    private double todayScore;
    private int totalServed;
    private int totalOnline;
    private double totalScore;

    public int getTodayServed() {
        return todayServed;
    }

    public void setTodayServed(int todayServed) {
        this.todayServed = todayServed;
    }

    public int getTodayOnline() {
        return todayOnline;
    }

    public void setTodayOnline(int todayOnline) {
        this.todayOnline = todayOnline;
    }

    public double getTodayScore() {
        return todayScore;
    }

    public void setTodayScore(double todayScore) {
        this.todayScore = todayScore;
    }

    public int getTotalServed() {
        return totalServed;
    }

    public void setTotalServed(int totalServed) {
        this.totalServed = totalServed;
    }

    public int getTotalOnline() {
        return totalOnline;
    }

    public void setTotalOnline(int totalOnline) {
        this.totalOnline = totalOnline;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }
}
