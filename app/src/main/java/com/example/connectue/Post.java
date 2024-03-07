package com.example.connectue;

public class Post {
    //use same name as we given while uploading post
    // TODO: add user and authentication related variables e.g. uid
    String pId, pDescription, pImage, pTime, pLikes, pComments, uid;

    public Post() {
    }

    public Post(String pId, String pDescription, String pImage, String pTime, String pLikes, String pComments, String uid) {
        this.pId = pId;
        this.pDescription = pDescription;
        this.pImage = pImage;
        this.pTime = pTime;
        this.pLikes = pLikes;
        this.pComments = pComments;
        this.uid = uid;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
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

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
