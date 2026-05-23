package com.group19.dto;

public class AdminFeedItem {
    private String title;
    private String meta;

    public AdminFeedItem() {
    }

    public AdminFeedItem(String title, String meta) {
        this.title = title;
        this.meta = meta;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }
}
