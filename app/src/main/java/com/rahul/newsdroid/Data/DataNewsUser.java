package com.rahul.newsdroid.Data;

public class DataNewsUser {

    public String image, name;

    public DataNewsUser(){
    }

    public DataNewsUser(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }
    public String getImage() {
        return image;
    }
}
