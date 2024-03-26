package com.example.connectue.interfaces;

public interface ItemExistsCallback {
    public void onSuccess(boolean exists);

    default void onFailure(Exception e) {};
}
