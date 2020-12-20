package com.example.pickingapp;

public class Model {

    private String title;
    private String desc;
    private String location;

    public Model(String title, String desc, String location) {
        this.title = title;
        this.desc = desc;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }
}
