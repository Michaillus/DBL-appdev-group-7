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
}
