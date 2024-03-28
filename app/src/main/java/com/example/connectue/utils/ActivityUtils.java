package com.example.connectue.utils;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.connectue.model.StudyUnit;

public class ActivityUtils {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "ActivityUtils";

    public static StudyUnit getCourse(AppCompatActivity activity, Bundle savedInstanceState) {
        String courseAsString;
        if (savedInstanceState == null) {
            Bundle extras = activity.getIntent().getExtras();
            if(extras == null) {
                courseAsString= "0#0#0#0#0#course";
                Log.e(TAG, "No course passed to the activity");
            } else {
                courseAsString= extras.getString("course");
            }
        } else {
            courseAsString= (String) savedInstanceState.getSerializable("course");
        }
        if (courseAsString == null) {
            courseAsString = "0#0#0#0#0#0course";
            Log.e(TAG, "No course passed to the activity");
        }
        return StudyUnit.stringToStudyUnit(courseAsString);
    }
}
