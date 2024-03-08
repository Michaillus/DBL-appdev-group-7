package com.example.connectue;

public class Post {
    //use same name as we given while uploading post
    // TODO: add user and authentication related variables e.g. uid
    String uName, pDescription, pImage, pLikes, pComments;

    public Post() {
    }

    public Post(String uName, String pDescription, String pImage, String pLikes, String pComments) {
        this.uName = uName;
        this.pDescription = pDescription;
        this.pImage = pImage;
        this.pLikes = pLikes;
        this.pComments = pComments;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getpDescription() {
        return pDescription;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }
}
