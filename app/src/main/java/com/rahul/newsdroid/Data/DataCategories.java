package com.rahul.newsdroid.Data;

public class DataCategories extends DataNewsPostID {

    public String name;
    int thumb;

    public DataCategories(String name, int thumb) {
        this.name = name;
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }
    public int getThumb() {
        return thumb;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setThumb(int thumb) {
        this.thumb = thumb;
    }
}