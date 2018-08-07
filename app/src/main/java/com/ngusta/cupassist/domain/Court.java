package com.ngusta.cupassist.domain;

public class Court {

    private double lat;

    private double lng;

    private String title;

    private String description;

    private String link;

    private int numCourts;

    private boolean hasNet;

    private boolean hasLines;

    private boolean hasAntennas;

    public Court() {
    }

    public Court(double lat, double lng, String title, String description, String link, int numCourts, boolean hasNet, boolean hasLines,
            boolean hasAntennas) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.description = description;
        this.link = link;
        this.numCourts = numCourts;
        this.hasNet = hasNet;
        this.hasLines = hasLines;
        this.hasAntennas = hasAntennas;
    }


    public Court(double lat, double lng, String title) {
        this(lat, lng, title, null, null, 1, false, false, false);
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

    public int getNumCourts() {
        return numCourts;
    }

    public boolean getHasNet() {
        return hasNet;
    }

    public boolean getHasLines() {
        return hasLines;
    }

    public boolean getHasAntennas() {
        return hasAntennas;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setNumCourts(int numCourts) {
        this.numCourts = numCourts;
    }

    public void setHasNet(boolean hasNet) {
        this.hasNet = hasNet;
    }

    public void setHasLines(boolean hasLines) {
        this.hasLines = hasLines;
    }

    public void setHasAntennas(boolean hasAntennas) {
        this.hasAntennas = hasAntennas;
    }
}
