package com.example.connectue.interfaces;

/**
 * Callback for an asynchronous method that check that an item exists in a database.
 */
public interface ItemExistsCallback {
    /**
     * Called when asynchronous method is finished successfully.
     * @param exists if item exists.
     */
    public void onSuccess(boolean exists);

    /**
     * Called when an exception occurred during the asynchronous method and it was interrupted.
     * @param e Exception.
     */
    default void onFailure(Exception e) {};
}
