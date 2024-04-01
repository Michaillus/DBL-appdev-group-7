package com.example.connectue.interfaces;

/**
 * Callback for an asynchronous method that downloads some data from the database.
 * @param <T> Model of the data downloaded.
 */
public interface ItemDownloadCallback<T> {
    /**
     * Called when asynchronous method is finished successfully.
     * @param data Model of the data downloaded.
     */
    public void onSuccess(T data);

    /**
     * Called when an exception occurred during the asynchronous method and it was interrupted.
     * @param e Exception.
     */
    default void onFailure(Exception e) {};
}
