package com.example.connectue;

import com.google.firebase.firestore.PropertyName;

public class Question {
    @PropertyName("reviewer")
    public String uName;
    @PropertyName("date")
    public String date;
    @PropertyName("text")
    public String uText;
    @PropertyName("question-likes")
    public int qLikes;
    @PropertyName("question-likeNum")
    public String qLikeNum;


    public Question(String uName, String date, String uText, int qLikes, String qLikeNum) {
        this.uName = uName;
        this.date = date;
        this.uText = uText;
        this.qLikes = qLikes;
        this.qLikeNum = qLikeNum;
    }

    public String getuName() { return uName; }

    public void setuName(String uName) { this.uName = uName; }

    public String getuText() { return uText; }

    public void setuText(String uText) { this.uText = uText; }

    public int getqLikes() { return qLikes; }

    public void setqLikes(int qLikes) { this.qLikes = qLikes; }

    public String getqLikeNum() { return qLikeNum; }

    public void setqLikeNum(String qLikeNum) { this.qLikeNum = qLikeNum; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }
}
