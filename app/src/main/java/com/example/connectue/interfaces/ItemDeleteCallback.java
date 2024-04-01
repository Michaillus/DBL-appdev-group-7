package com.example.connectue.interfaces;

public interface ItemDeleteCallback {
    public void onSuccess();

    default void onFailure(Exception e) {};
}
