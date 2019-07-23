package com.rahul.newsdroid.Data;

public class DataNotification {

    public String post_id, post_title, post_description, post_category, post_date, post_image, post_like, post_youtube, youtube_video;

    public DataNotification(String post_id, String post_title, String post_description, String post_category, String post_date, String post_image, String post_like, String youtube_video, String post_youtube) {
        this.post_id = post_id;
        this.post_title = post_title;
        this.post_description = post_description;
        this.post_category = post_category;
        this.post_date = post_date;
        this.post_image = post_image;
        this.post_like = post_like;
        this.post_youtube = post_youtube;
        this.youtube_video = youtube_video;
    }
}
