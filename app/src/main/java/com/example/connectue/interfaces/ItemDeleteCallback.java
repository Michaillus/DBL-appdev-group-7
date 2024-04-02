package com.example.connectue.interfaces;

/**
 * Callback for an asynchronous method that deletes some data from the database.
 */
public interface ItemDeleteCallback {
    /**
     * Called when asynchronous method is finished successfully.
     */
    public void onSuccess();

    /**
     * Called when an exception occurred during the asynchronous method and it was interrupted.
     * @param e Exception.
     */
    default void onFailure(Exception e) {};
}
