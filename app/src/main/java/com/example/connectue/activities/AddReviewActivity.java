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

import com.example.connectue.model.Review;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.utils.ActivityUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connectue.R;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.managers.ReviewManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddReviewActivity extends AppCompatActivity {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "AddReviewActivity";

    ReviewManager reviewManager;
    EditText reviewDescription;
    Button publishReviewBtn;
    FloatingActionButton backBtn;
    ImageButton star_1, star_2, star_3, star_4, star_5;
    Long stars;
    String text;

    /**
     * Method is called when the activity is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        // Retrieve study unit passed from the previous activity / fragment.
        StudyUnit studyUnit = ActivityUtils.getStudyUnit(this, savedInstanceState);

        // Initializing review manager
        reviewManager = new ReviewManager(FirebaseFirestore.getInstance(),
                studyUnit.getReviewCollectionName(),
                studyUnit.getReviewLikeCollectionName(),
                studyUnit.getReviewDislikeCollectionName(),
                studyUnit.getReviewCommentCollectionName());

        initializeViews();

        // Set listeners for star rating buttons.
        star_1.setOnClickListener(v -> selectStarRating(1));
        star_2.setOnClickListener(v -> selectStarRating(2));
        star_3.setOnClickListener(v -> selectStarRating(3));
        star_4.setOnClickListener(v -> selectStarRating(4));
        star_5.setOnClickListener(v -> selectStarRating(5));

        // Set listeners for publish a review button.
        publishReviewBtn.setOnClickListener(v -> publishReview(studyUnit));

        // Set listeners for back button.
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Initialize the UI components of the adding review page.
     */
    void initializeViews() {
        reviewDescription = findViewById(R.id.reviewDescription);
        publishReviewBtn = findViewById(R.id.publishReviewBtn);
        backBtn = findViewById(R.id.back_Btn);
        star_1 = findViewById(R.id.star1);
        star_2 = findViewById(R.id.star2);
        star_3 = findViewById(R.id.star3);
        star_4 = findViewById(R.id.star4);
        star_5 = findViewById(R.id.star5);
    }

    /**
     * Method to handle star rating selection after clicking on stars on the adding review page.
     * @param rating The number of stars clicked
     */
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

    }

    /**
     * Method to publish a review and upload to database with stars. If no stars selected, a message
     * called for selecting a star rating will be present.
     * @param studyUnit The unit - review to be published to database.
     */
    private void publishReview(StudyUnit studyUnit) {
        publishReviewBtn.setEnabled(false);
        reviewDescription.setEnabled(false);
        text = reviewDescription.getText().toString().trim();
        // Check if the user has selected a star rating
        if (stars != null) {
            // Upload the review to Firestore
            uploadToFirestore(text, stars, studyUnit);
            // Pass the selected star rating back to ReviewsFragment

        } else {
            // Inform the user to select a star rating
            Toast.makeText(this, "Please select a star rating", Toast.LENGTH_SHORT).show();
            // Re-enable the button and text field for editing
            publishReviewBtn.setEnabled(true);
            reviewDescription.setEnabled(true);
        }
    }

    /**
     * Method to upload the review to database. After publishing a review successfully, a message to
     * present this situation will be present and the page will go to the review view page of the specific
     * course. Also, the review is uploaded to database.
     *
     * @param text The text field for editing the contents of a review
     * @param stars The rating stars selected
     * @param studyUnit The unit - review.
     */
    private void uploadToFirestore(String text, Long stars, StudyUnit studyUnit) {
        String publisherId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Review review = new Review(publisherId, text, stars, studyUnit.getId());

        reviewManager.addReview(review, studyUnit.getType(), new ItemUploadCallback() {
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

