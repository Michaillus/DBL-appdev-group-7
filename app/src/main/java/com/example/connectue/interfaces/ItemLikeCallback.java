package com.example.connectue.interfaces;

public interface ItemLikeCallback {
    public void onSuccess(Boolean isLiked);

    default void onFailure(Exception e) {};
}
