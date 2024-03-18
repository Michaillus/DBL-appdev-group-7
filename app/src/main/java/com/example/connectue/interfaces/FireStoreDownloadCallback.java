package com.example.connectue.interfaces;

public interface FireStoreDownloadCallback<T> {
    public void onSuccess(T data);

    public void onFailure(Exception e);
}
