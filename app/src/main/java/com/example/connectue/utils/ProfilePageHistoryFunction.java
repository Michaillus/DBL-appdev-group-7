package com.example.connectue.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.connectue.R;
import com.example.connectue.activities.PostHistoryActivity;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Helper class to handle the functionality related to displaying user's history on the profile page.
 */
public class ProfilePageHistoryFunction {
//    private final Context context;
    private final Activity activity;
    private final View view;
    private final DocumentSnapshot document;
    private long role = 2;
    private Button postHisBtn;
    private Button reviewHisBtn;

    /**
     * Constructor to initialize the ProfilePageHistoryFunction.
     *
     * @param context  The context in which the ProfilePageHistoryFunction is operating
     * @param activity The activity context
     * @param view     The view associated with the profile page
     * @param document The document snapshot containing user information
     */
    public ProfilePageHistoryFunction(Context context, Activity activity, View view, DocumentSnapshot document) {
//        this.context = context;
        this.activity = activity;
        this.view = view;
        this.document = document;
        parseDocument();
        if (context != null) {
            initComponents();
        }
    }

    /**
     * Parse the document snapshot to extract user role information.
     */
    private void parseDocument() {
        // Get the user's role from the document
        role = document.getLong(General.ROLE) == null ? 2: document.getLong(General.ROLE);
    }

    /**
     * Initialize the components of the profile page history section.
     * This includes setting up buttons for post history and review history.
     * Depending on the user's role, certain buttons may be hidden.
     */
    private void initComponents() {
        // Initialize buttons for post history and review history
        postHisBtn = view.findViewById(R.id.btn_postHistory);
        reviewHisBtn  = view.findViewById(R.id.btn_reviewHistory);

        // Set the visibility pf post history button and review history button based on user's role
        if (General.isGuest(role)) {
            postHisBtn.setVisibility(View.GONE);
            reviewHisBtn.setVisibility(View.INVISIBLE);
        }

        // Call button initialize functions
        initPostHisButton();
        initReviewHisButton();
    }

    /**
     * Initialize the button for displaying post history.
     * Clicking this button opens the post history activity.
     */
    private void initPostHisButton() {
        // set text for post history button
        postHisBtn.setText("Post History");
        postHisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the post history activity
                Intent history = new Intent(activity, PostHistoryActivity.class);
                history.putExtra("collection", General.POSTCOLLECTION);
                activity.startActivity(history);
            }
        });
    }

    /**
     * Initialize the button for displaying course review history.
     * Clicking this button opens the course review history activity.
     */
    private void initReviewHisButton() {
        // set text for review history button
        reviewHisBtn.setText("Course Review History");
        reviewHisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the course review history activity
                Intent history = new Intent(activity,PostHistoryActivity.class);
                history.putExtra("collection", General.COURSEREVIEWCOLLECTION);
                activity.startActivity(history);
            }
        });
    }
}
