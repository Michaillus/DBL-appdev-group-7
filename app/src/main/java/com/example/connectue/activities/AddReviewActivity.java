package com.example.connectue.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.Manifest;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connectue.R;
import com.example.connectue.interfaces.FireStoreUploadCallback;
import com.example.connectue.managers.ReviewManager;
import com.example.connectue.model.Review;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddReviewActivity extends AppCompatActivity {

    ReviewManager reviewManager;
    EditText reviewDescription;
    Button publishReviewBtn;
    FloatingActionButton backBtn;
    ImageButton star_1, star_2, star_3, star_4, star_5;
    Long stars;
    String text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        // Initializing review manager
        reviewManager = new ReviewManager(FirebaseFirestore.getInstance(), Review.REVIEW_COLLECTION_NAME,
                Review.REVIEW_LIKE_COLLECTION_NAME,
                Review.REVIEW_DISLIKE_COLLECTION_NAME,
                Review.REVIEW_COMMENT_COLLECTION_NAME);

        reviewDescription = findViewById(R.id.reviewDescription);
        publishReviewBtn = findViewById(R.id.publishReviewBtn);
        backBtn = findViewById(R.id.back_Btn);
        star_1 = findViewById(R.id.star1);
        star_2 = findViewById(R.id.star2);
        star_3 = findViewById(R.id.star3);
        star_4 = findViewById(R.id.star4);
        star_5 = findViewById(R.id.star5);

        // Set listeners for star rating buttons
        star_1.setOnClickListener(v -> selectStarRating(1));
        star_2.setOnClickListener(v -> selectStarRating(2));
        star_3.setOnClickListener(v -> selectStarRating(3));
        star_4.setOnClickListener(v -> selectStarRating(4));
        star_5.setOnClickListener(v -> selectStarRating(5));

        publishReviewBtn.setOnClickListener(v -> publishReview());
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddReviewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    // Method to handle star rating selection
    private void selectStarRating(int rating) {
        stars = (long) rating;
        // Update UI to reflect the selected star rating
    }

    private void publishReview() {
        publishReviewBtn.setEnabled(false);
        reviewDescription.setEnabled(false);
        text = reviewDescription.getText().toString().trim();
        // Check if the user has selected a star rating
        if (stars != null) {
            // Upload the review to Firestore
            uploadToFirestore(text, stars);
        } else {
            // Inform the user to select a star rating
            Toast.makeText(this, "Please select a star rating", Toast.LENGTH_SHORT).show();
            // Re-enable the button and text field for editing
            publishReviewBtn.setEnabled(true);
            reviewDescription.setEnabled(true);
        }
    }

    private void uploadToFirestore(String text, Long stars) {
        String publisherId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Review review = new Review(publisherId, text, stars);

        reviewManager.upload(review, new FireStoreUploadCallback() {
            @Override
            public void onSuccess() {
                Log.i("Upload review", "Review is uploaded successfully");
                Toast.makeText(AddReviewActivity.this,
                        "Review is published successfully", Toast.LENGTH_SHORT).show();

                Intent intentReviews = new Intent(AddReviewActivity.this, MainActivity.class);
                startActivity(intentReviews);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Upload review", "Failed to upload the review: " + e.getMessage());
                Toast.makeText(AddReviewActivity.this,
                        "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }

