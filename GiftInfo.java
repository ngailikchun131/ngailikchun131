package com.example.mall;

public class GiftInfo {
    private String name, url;
    private int point;

    public GiftInfo() {
    }

    public GiftInfo(String name, int point, String url) {
        this.name = name;
        this.point = point;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public int getPoint() {
        return point;
    }

    public String getUrl() { return url; }
}
