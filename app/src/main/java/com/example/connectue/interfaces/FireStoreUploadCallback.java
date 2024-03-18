package com.example.connectue.interfaces;

public interface FireStoreUploadCallback {
    public void onSuccess();

    default void onFailure(Exception e) {};
}
