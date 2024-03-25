package com.example.connectue.interfaces;

public interface FireStoreLikeCallback {
    public void onSuccess(Boolean isLiked);

    default void onFailure(Exception e) {};
}
