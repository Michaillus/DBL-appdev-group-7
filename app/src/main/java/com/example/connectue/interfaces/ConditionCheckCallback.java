package com.example.connectue.interfaces;

/**
 * Callback for an asynchronous method that check that some condition is satisfied.
 */
public interface ConditionCheckCallback {

    /**
     * Called when asynchronous method is finished successfully.
     * @param conditionSatisfied if condition is satisfied.
     */
    public void onSuccess(boolean conditionSatisfied);

    /**
     * Called when an exception occurred during the asynchronous method and it was interrupted.
     * @param e Exception.
     */
    default void onFailure(Exception e) {};
}
