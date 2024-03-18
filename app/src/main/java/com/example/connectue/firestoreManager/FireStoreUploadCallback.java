package com.example.connectue.firestoreManager;

public interface FireStoreUploadCallback {
    public void onSuccess();

    default void onFailure(Exception e) {};
}
