package com.example.mall;

public class MyGiftInfo {
    private String giftName, url,userID;
    private boolean receive;

    public MyGiftInfo() {
    }

    public MyGiftInfo(String giftName, boolean receive, String url, String userID) {
        this.giftName = giftName;
        this.receive = receive;
        this.userID = userID;
        this.url = url;
    }

    public String getUserID() {
        return userID;
    }

    public String getGiftName() {
        return giftName;
    }

    public boolean isReceive() {
        return receive;
    }

    public String getUrl() { return url; }
}
