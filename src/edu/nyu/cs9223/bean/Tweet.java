package edu.nyu.cs9223.bean;

import twitter4j.GeoLocation;

import java.util.Date;

public final class Tweet {
    private final long id;
    private final String text;
    private final String username;
    private final String date;
    private final double[] location;
    public Tweet(long id, String username, String text, String date, GeoLocation location) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.date = date;
        this.location = new double[] {location.getLatitude(), location.getLongitude()};
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public double[] getLocation() {
        return location;
    }
}
