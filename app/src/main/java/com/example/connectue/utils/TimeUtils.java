package com.example.connectue.utils;

import java.util.Date;
import android.text.format.DateUtils;

/**
 * Set the publish time format for posts, comments, reviews, etc.
 * The Date object will be convert into String such as one day ago, one week ago, etc.
 */
public class TimeUtils {

    // Method to display time ago
    public static String getTimeAgo(Date date) {
        long now = System.currentTimeMillis();
        long time = date.getTime();
        long difference = now - time;

        // Calculate the appropriate time unit
        if (difference < DateUtils.MINUTE_IN_MILLIS) {
            return "just now";
        } else if (difference < DateUtils.HOUR_IN_MILLIS) {
            return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS).toString();
        } else if (difference < DateUtils.DAY_IN_MILLIS) {
            return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.HOUR_IN_MILLIS).toString();
        } else if (difference < DateUtils.WEEK_IN_MILLIS) {
            return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.DAY_IN_MILLIS).toString();
        } else {
            return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.WEEK_IN_MILLIS).toString();
        }
    }
}
