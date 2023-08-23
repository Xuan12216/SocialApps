package com.example.socialapps.bottomMenu;

import java.util.ArrayList;

public class Post {
    private String text;
    private ArrayList<String> images;
    private ArrayList<String> video;
    private String userId;
    private String userProPic;
    private String userName;

    public Post(String postText, ArrayList<String> postImages, String profilePic, String userName, ArrayList<String> postVideos) {
        this.text = postText;
        this.images = postImages;
        this.userProPic = profilePic;
        this.userName = userName;
        this.video = postVideos;
    }

    // Getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getVideo() {
        return video;
    }

    public void setVideo(ArrayList<String> video) {
        this.video = video;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProPic() {
        return userProPic;
    }

    public void setUserProPic(String userProPic) {
        this.userProPic = userProPic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}