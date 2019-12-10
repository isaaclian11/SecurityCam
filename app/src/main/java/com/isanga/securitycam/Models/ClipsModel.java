package com.isanga.securitycam.Models;


import java.io.File;

//For now, title is good enough.
public class ClipsModel {
    private String title;
    private File thumbnail;
    public ClipsModel(String title, File file) {
        this.title = title;
        this.thumbnail = file;
    }

    public String getTitle() {
        return title;
    }

    public File getThumbnail() {
        return thumbnail;
    }

}
