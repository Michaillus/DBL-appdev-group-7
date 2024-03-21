package com.example.connectue.interfaces;

public interface FireStoreDownloadCallback<T> {
    public void onSuccess(T data);

    default void onFailure(Exception e) {};
}
