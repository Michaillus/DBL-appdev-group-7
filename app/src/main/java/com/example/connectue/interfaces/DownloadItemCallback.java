package com.example.connectue.interfaces;

public interface DownloadItemCallback<T> {
    public void onSuccess(T data);

    default void onFailure(Exception e) {};
}
