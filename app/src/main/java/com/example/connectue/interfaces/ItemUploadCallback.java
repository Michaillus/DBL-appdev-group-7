package com.example.connectue.interfaces;

public interface ItemUploadCallback {
    public void onSuccess();

    default void onFailure(Exception e) {};
}
