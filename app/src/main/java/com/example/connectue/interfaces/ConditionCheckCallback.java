package com.example.connectue.interfaces;

public interface ConditionCheckCallback {
    public void onSuccess(boolean conditionSatisfied);

    default void onFailure(Exception e) {};
}
