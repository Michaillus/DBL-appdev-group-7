package com.example.connectue.interfaces;

public interface ItemDownloadCallback<T> {
    public void onSuccess(T data);

    default void onFailure(Exception e) {};
}
