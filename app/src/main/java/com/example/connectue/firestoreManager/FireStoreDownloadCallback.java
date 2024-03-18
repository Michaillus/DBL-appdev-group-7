package com.example.connectue.firestoreManager;

public interface FireStoreDownloadCallback<T> {
    public void onSuccess(T data);

    public void onFailure(Exception e);
}
