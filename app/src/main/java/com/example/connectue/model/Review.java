package com.example.connectue.model;

import com.google.firebase.firestore.PropertyName;

public class Review {

    @PropertyName("reviewer")
    public String uName;
    @PropertyName("text")
    public String uText;
    @PropertyName("date")
    public String date;
    @PropertyName("likes")
    public int pLikes;
    @PropertyName("dislikes")
    public int pDislikes;
    @PropertyName("ratings")
    public int stars;
    @PropertyName("likeNum")
    public String likeNum;
    @PropertyName("dislikeNum")
    public String dislikeNum;

    // Constructor
    public Review(String uName, String date, String uText, int pLikes, int pDislikes, int stars, String likeNum, String dislikeNum) {
        this.uName = uName;
        this.date = date;
        this.uText = uText;
        this.pLikes = pLikes;
        this.pDislikes = pDislikes;
        this.stars = stars;
        this.likeNum = likeNum;
        this.dislikeNum = dislikeNum;
    }


    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuText() {
        return uText;
    }

    public void setuText(String uText) {
        this.uText = uText;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public int getpLikes() {
        return pLikes;
    }

    public void setpLikes(int pLikes) {
        this.pLikes = pLikes;
    }

    public int getpDislikes() {
        return pDislikes;
    }

    public void setpDislikes(int pDislikes) {
        this.pDislikes = pDislikes;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }


    public String getLikeNum() { return likeNum; }

    public void setLikeNum(String likeNum) { this.likeNum = likeNum; }

    public String getDislikeNum() { return dislikeNum; }

    public void setDislikeNum(String dislikeNum) { this.dislikeNum = dislikeNum; }
}
