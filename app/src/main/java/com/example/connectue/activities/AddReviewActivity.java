package com.example.connectue.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connectue.R;
import com.example.connectue.managers.ReviewManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddReviewActivity extends AppCompatActivity {


    CollectionReference reviews;
    ReviewManager reviewManager;
    EditText reviewDescription;
    Button publishReviewBtn;
    ImageButton star_1, star_2, star_3, star_4, star_5;
    Long stars;
    String text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        reviews = FirebaseFirestore.getInstance().collection("reviews");

        // Initializing post manager
        reviewManager = new ReviewManager(FirebaseFirestore.getInstance(), "reviews",
                "reviews-likes", "reviews-dislikes", "");

        reviewDescription = findViewById(R.id.reviewDescription);
        publishReviewBtn = findViewById(R.id.publishReviewBtn);
        star_1 = findViewById(R.id.star1);
        star_2 = findViewById(R.id.star2);
        star_3 = findViewById(R.id.star3);
        star_4 = findViewById(R.id.star4);
        star_5 = findViewById(R.id.star5);

        publishReviewBtn.setOnClickListener(v -> {
            publishReviewBtn.setEnabled(false);
            reviewDescription.setEnabled(false);
            String description = reviewDescription.getText().toString().trim();
            //publishPost(description, imageUri);
        });


    }
}
