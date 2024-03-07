package com.example.connectue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostsActivity extends AppCompatActivity {

    // Number of posts loaded at once to the feed.
    final int postsPerChunk = 3;
    // Query for loading posts from the database.
    Query postsQuery;
    // Last post that was loaded from the database.
    DocumentSnapshot lastVisiblePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        NavigationBar.addNavigationBarActivitySwitch(this);

        // !!! Currently the button loads loads new posts
        ImageView button = findViewById(R.id.button);
        button.setOnClickListener(v -> loadMorePosts());

        // Create a query for loading posts from the database.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        postsQuery = db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING);
        loadPosts();
    }

    /**
     * Load first postsPerChunk number of posts from database.
     */
    void loadPosts() {
        postsQuery.limit(postsPerChunk).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.isEmpty()) {
                lastVisiblePost = snapshot.getDocuments().get(snapshot.size() - 1);
                displayPosts(snapshot);
            }
        });
    }

    /**
     * Load next postsPerChunk number of posts from database.
     */
    void loadMorePosts() {
        Query nextQuery = postsQuery.startAfter(lastVisiblePost).limit(postsPerChunk);
        nextQuery.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.isEmpty()) {
                lastVisiblePost = snapshot.getDocuments().get(snapshot.size() - 1);
                displayPosts(snapshot);
            }
        });
    }

    // TODO: make a function to display posts in UI
    // !!! for now it just outputs post description logs.
    ArrayList<DocumentSnapshot> postList = new ArrayList<>();
    /**
     * Displays posts that were loaded from the database to the post feed in UI.
     * @param snapshot object containing all the posts that were loaded from database.
     */
    void displayPosts(QuerySnapshot snapshot) {
        List<DocumentSnapshot> documentSnapshots = snapshot.getDocuments();
        postList.addAll(documentSnapshots);
        System.out.println("3 posts loaded:");
        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
            System.out.println(documentSnapshot.get("text"));
        }
        System.out.println("All posts:");
        for(DocumentSnapshot documentSnapshot : postList) {
            System.out.println(documentSnapshot.get("text"));
        }
        System.out.println("----------------");
    }
}

