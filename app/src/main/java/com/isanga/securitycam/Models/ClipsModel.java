package com.isanga.securitycam.Models;

//For now, title is good enough.
//Might add thumbnails later
public class ClipsModel {
    private String title;

    public ClipsModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
