package edu.nyu.cs9223.bean;

import twitter4j.GeoLocation;

import java.util.Date;

public final class Tweet {
    private final long id;
    private final String text;
    private final String username;
    private final Date date;
    private final GeoLocation location;
    public Tweet(long id, String username, String text, Date date, GeoLocation location) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.date = date;
        this.location = location;
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

    public Date getDate() {
        return date;
    }

    public GeoLocation getLocation() {
        return location;
    }
}
