package com.example.connectue.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.connectue.R;
import com.example.connectue.activities.PostHistoryActivity;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfilePageHistoryFunction {
//    private final Context context;
    private final Activity activity;
    private final View view;
    private final DocumentSnapshot document;
    private long role = 2;
    private Button postHisBtn;
    private Button reviewHisBtn;

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

    private void parseDocument() {
        role = document.getLong(General.ROLE) == null ? 2: document.getLong(General.ROLE);
    }

    private void initComponents() {
        postHisBtn = view.findViewById(R.id.btn_postHistory);
        reviewHisBtn  = view.findViewById(R.id.btn_reviewHistory);

        if (General.isGuest(role)) {
            postHisBtn.setVisibility(View.GONE);
            reviewHisBtn.setVisibility(View.INVISIBLE);
        }

        initPostHisButton();
        initReviewHisButton();
    }

    private void initPostHisButton() {
        postHisBtn.setText("Post History");
        postHisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history = new Intent(activity, PostHistoryActivity.class);
                history.putExtra("collection", General.POSTCOLLECTION);
                activity.startActivity(history);
            }
        });
    }

    private void initReviewHisButton() {
        reviewHisBtn.setText("Course Review History");
        reviewHisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history = new Intent(activity,PostHistoryActivity.class);
                history.putExtra("collection", General.COURSEREVIEWCOLLECTION);
                activity.startActivity(history);
            }
        });
    }
}
