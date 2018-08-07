package com.ngusta.cupassist.domain;

public class Court {

    private double lat;

    private double lng;

    private String title;

    private String description;

    private String link;

    public Court() {
    }

    public Court(double lat, double lng, String title, String description, String link) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public boolean hasLink() {
        return link != null;
    }
}
