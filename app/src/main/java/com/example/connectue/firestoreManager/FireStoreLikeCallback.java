package com.example.connectue.firestoreManager;

public interface FireStoreLikeCallback {
    public void onSuccess(Boolean isLiked);

    public void onFailure(Exception e);
}
