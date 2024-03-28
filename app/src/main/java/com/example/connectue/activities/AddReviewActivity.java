package com.example.connectue.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.connectue.model.CourseReview;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connectue.R;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.managers.CourseReviewManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddReviewActivity extends AppCompatActivity {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "AddReviewActivity";

    CourseReviewManager courseReviewManager;
    EditText reviewDescription;
    Button publishReviewBtn;
    FloatingActionButton backBtn;
    ImageButton star_1, star_2, star_3, star_4, star_5;
    RatingBar ratingBar;
    Long stars;
    String text;

    private String courseId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        // Initializing review manager
        courseReviewManager = new CourseReviewManager(FirebaseFirestore.getInstance(), CourseReview.COURSE_REVIEW_COLLECTION_NAME,
                CourseReview.COURSE_REVIEW_LIKE_COLLECTION_NAME,
                CourseReview.COURSE_REVIEW_DISLIKE_COLLECTION_NAME,
                CourseReview.COURSE_REVIEW_COMMENT_COLLECTION_NAME);

        reviewDescription = findViewById(R.id.reviewDescription);
        publishReviewBtn = findViewById(R.id.publishReviewBtn);
        ratingBar = findViewById(R.id.ratingBar);
        backBtn = findViewById(R.id.back_Btn);
        star_1 = findViewById(R.id.star1);
        star_2 = findViewById(R.id.star2);
        star_3 = findViewById(R.id.star3);
        star_4 = findViewById(R.id.star4);
        star_5 = findViewById(R.id.star5);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                courseId= "";
            } else {
                courseId= extras.getString("courseId");
            }
        } else {
            courseId= (String) savedInstanceState.getSerializable("courseId");
        }



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
                finish();
            }
        });

    }

    // Method to handle star rating selection
    private void selectStarRating(int rating) {
        stars = (long) rating;
        // Update UI to reflect the selected star rating
        // Reset all star buttons to default state
        star_1.setImageResource(R.drawable.star_empty);
        star_2.setImageResource(R.drawable.star_empty);
        star_3.setImageResource(R.drawable.star_empty);
        star_4.setImageResource(R.drawable.star_empty);
        star_5.setImageResource(R.drawable.star_empty);


        // Set selected star buttons based on the rating
        switch (rating) {
            case 1:
                star_1.setImageResource(R.drawable.star_filled);
                break;
            case 2:
                star_1.setImageResource(R.drawable.star_filled);
                star_2.setImageResource(R.drawable.star_filled);
                break;
            case 3:
                star_1.setImageResource(R.drawable.star_filled);
                star_2.setImageResource(R.drawable.star_filled);
                star_3.setImageResource(R.drawable.star_filled);
                break;
            case 4:
                star_1.setImageResource(R.drawable.star_filled);
                star_2.setImageResource(R.drawable.star_filled);
                star_3.setImageResource(R.drawable.star_filled);
                star_4.setImageResource(R.drawable.star_filled);
                break;
            case 5:
                star_1.setImageResource(R.drawable.star_filled);
                star_2.setImageResource(R.drawable.star_filled);
                star_3.setImageResource(R.drawable.star_filled);
                star_4.setImageResource(R.drawable.star_filled);
                star_5.setImageResource(R.drawable.star_filled);
                break;
        }

        //update the ratings for each course review

    }

    private void publishReview() {
        publishReviewBtn.setEnabled(false);
        reviewDescription.setEnabled(false);
        text = reviewDescription.getText().toString().trim();
        // Check if the user has selected a star rating
        if (stars != null) {
            // Upload the review to Firestore
            uploadToFirestore(text, stars);
            // Pass the selected star rating back to ReviewsFragment

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
        CourseReview courseReview = new CourseReview(publisherId, text, stars, courseId);

        courseReviewManager.addReview(courseReview, new ItemUploadCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Review is added successfully");
                Toast.makeText(AddReviewActivity.this,
                        "Review is published successfully", Toast.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to upload the review: ", e);
                Toast.makeText(AddReviewActivity.this,
                        "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }

