package com.example.connectue.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.connectue.utils.General;
import com.example.connectue.R;
import com.example.connectue.adapters.RecyclerViewAdapter;
import com.example.connectue.model.Post;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostHistoryActivity extends AppCompatActivity {
    private final static String TAG = "postHis";
    Button backBtn;
    private FirebaseFirestore db;
//    TODO: the following is the text version
    private List<Post> postList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_history);

        db = FirebaseFirestore.getInstance();
        postList = new ArrayList<>();
        loadUserPost();

//        recyclerView = findViewById(R.id.postList_rv);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        mAdapter = new RecyclerViewAdapter(postList, PostHistoryActivity.this);

        backBtn = findViewById(R.id.btn_back);
        initBcakButton();
    }

    //TODO: test version
    private void loadUserPost() {
            db.collection(General.POSTCOLLECTION).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getString(General.PUBLISHER);
                                Post post = new Post(document.getId(),
                                        document.getString("publisher"),
                                        document.getString("text"),
                                        document.getString("photoULR"),
                                        0L,
                                        document.getLong("likes"),
                                        document.getLong("comments"),
                                        document.getTimestamp("timestamp").toDate());
                                postList.add(post);
                            }
                        } else {
                            Log.i(TAG,"failed to get all documents");
                        }
                        Log.i(TAG, ""+postList.size());
                        recyclerView = findViewById(R.id.postList_rv);
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(this);
                        recyclerView.setLayoutManager(layoutManager);
                        mAdapter = new RecyclerViewAdapter(postList, PostHistoryActivity.this);
                        recyclerView.setAdapter(mAdapter);
                        Log.i(TAG,"end of init");
                    });

    }

    private void initBcakButton() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOtherActivity(MainActivity.class);
            }
        });
    }

    private void toOtherActivity(Class activity) {
        Intent loading = new Intent(PostHistoryActivity.this, activity);
        PostHistoryActivity.this.startActivity(loading);
        PostHistoryActivity.this.finish();
    }


}