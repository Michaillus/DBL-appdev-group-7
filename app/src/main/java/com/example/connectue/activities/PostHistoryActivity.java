package com.example.connectue.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.connectue.model.Interactable;
import com.example.connectue.utils.General;
import com.example.connectue.R;
import com.example.connectue.adapters.PostReviewHistoryAdapter;
import com.example.connectue.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying the post history of the current user.
 */
public class PostHistoryActivity extends AppCompatActivity {
    private final static String TAG = "postHis";
    Button backBtn;
    private FirebaseFirestore db;
    private CollectionReference postRef;
    private FirebaseUser user;
    private List<Interactable> postList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    /**
     * Initializes the activity when it is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_history);
        // Retrieve the collection name passed from the previous activity
        String collectionName = getIntent().getStringExtra("collection");

        // If the collection name is not null, initialize Firestore and load user posts
        if (collectionName != null) {
            db = FirebaseFirestore.getInstance();
            postRef = db.collection(collectionName);
            postList = new ArrayList<>();
            loadUserPost(collectionName);
        }

        // Initialize back button
        backBtn = findViewById(R.id.btn_back);
        initBackButton();
    }

    /**
     * Loads posts published by the current user from the specified collection.
     *
     * @param collectionName The name of the collection from which to load user posts.
     */
    private void loadUserPost(String collectionName) {
        // Get current authenticated user
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Create a Firestore query to retrieve posts where the publisher is the current user
        Query query = postRef.whereEqualTo("publisher", user.getUid());

            query.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Iterate through the query results
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getString(General.PUBLISHER);
                                Interactable post = new Post(document.getId(),
                                        document.getString("publisher"),
                                        document.getString("text"),
                                        document.getString("photoURL"),
                                        0L,
                                        document.getLong("likes"),
                                        document.getLong("comments"),
                                        document.getTimestamp("timestamp").toDate());

                                // Add the post to the list of user posts
                                postList.add(post);
                            }
                        } else {
                            // Log an error message if the query fails
                            Log.i(TAG,"failed to get all documents");
                        }
                        // Log the size of the post list
                        Log.i(TAG, ""+postList.size());
                        // Initialize the RecyclerView and its adapter to display user posts
                        recyclerView = findViewById(R.id.postList_rv);
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(this);
                        recyclerView.setLayoutManager(layoutManager);
                        mAdapter = new PostReviewHistoryAdapter(
                                postList, PostHistoryActivity.this, collectionName);
                        recyclerView.setAdapter(mAdapter);
                        // Log the end of the initialization process
                        Log.i(TAG,"end of init");
                    });

    }

    /**
     * Initialize backButton.
     */
    private void initBackButton() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}