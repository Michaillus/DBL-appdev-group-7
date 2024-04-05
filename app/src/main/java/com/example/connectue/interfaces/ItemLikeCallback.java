package com.example.connectue.interfaces;

/**
 * Callback for an asynchronous method that check that a item is liked.
 */
public interface ItemLikeCallback {
    /**
     * Called when asynchronous method is finished successfully.
     * @param isLiked if item is liked.
     */
    public void onSuccess(Boolean isLiked);

    /**
     * Called when an exception occurred during the asynchronous method and it was interrupted.
     * @param e Exception.
     */
    default void onFailure(Exception e) {};
}
