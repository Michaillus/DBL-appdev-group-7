package com.example.connectue.utils;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.connectue.model.StudyUnit;

/**
 * Class for general helper methods for activities.
 */
public class ActivityUtils {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "ActivityUtils";

    /**
     * Retrieves a study unit that was passed to the activity through extras.
     * @param activity Current activity.
     * @param savedInstanceState Bundle of the activity, that may contain student unit.
     * @return Student unit.
     */
    public static StudyUnit getStudyUnit(AppCompatActivity activity, Bundle savedInstanceState) {
        String courseAsString;
        if (savedInstanceState == null) {
            Bundle extras = activity.getIntent().getExtras();
            if(extras == null) {
                // No study unit was passed.
                courseAsString= "0#0#0#0#0#course";
                Log.e(TAG, "No course passed to the activity");
            } else {
                // Study unit is in extras.
                courseAsString= extras.getString("course");
                Log.d(TAG, "course passed to activity");
            }
        } else {
            // Study unit is in the bundle.
            courseAsString= (String) savedInstanceState.getSerializable("course");
        }
        if (courseAsString == null) {
            // Passed course string is null.
            courseAsString = "0#0#0#0#0#0course";
            Log.e(TAG, "No course passed to the activity");
        }
        Log.d(TAG, courseAsString);
        return StudyUnit.stringToStudyUnit(courseAsString);
    }
}
