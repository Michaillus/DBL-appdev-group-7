package com.example.connectue.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.R;
import com.example.connectue.interfaces.ItemLikeCallback;
import com.example.connectue.managers.ReviewManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.model.Review;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.utils.General;
import com.example.connectue.model.User;
import com.example.connectue.utils.TimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    List<Review> reviewList;
    private String currentUid;
    private FragmentManager fragmentManager;
    private ReviewManager reviewManager;

    /**
     *  Class tag for logs.
     */
    private String TAG = "TestAdapterReviews";

    /**
     * Study unit of the current page.
     */
    private final StudyUnit studyUnit;

    /**
     * Constructor of a review adapter with the provided data.
     * @param reviewList The list of reviews to be displayed
     * @param fragmentManager The fragment manager for handling fragments within the adapter
     * @param studyUnit The study unit associated with the reviews
     */
    public ReviewAdapter(List<Review> reviewList, FragmentManager fragmentManager,
                         StudyUnit studyUnit) {
        this.reviewList = reviewList;
        this.fragmentManager = fragmentManager;
        this.studyUnit = studyUnit;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent
     * an item.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (Giving a look to our rows)

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.study_unit_review_row, parent, false);
        return new ReviewAdapter.MyViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the ViewHolder to reflect the item at the given position.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MyViewHolder holder, int position) {
        // assign values to the views we created in the study_unit_review_row file
        // based on the position of the recycler view

        Review review = reviewList.get(position);
        holder.bind(review);
        holder.ratingBar.setIsIndicator(true);
        holder.ratingBar.setRating(review.getStars());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter's data set.
     */
    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items you want to display
        return reviewList.size();
    }

    /**
     * The public class to grab the view contents, bind the views, and set the interaction.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        ReviewManager reviewManager;
        // grab the views from study_unit_review_row file
        ImageView reviewLike, reviewDislike;
        TextView reviewerName;
        TextView description;
        TextView date;
        TextView likeNumber, dislikeNumber;
        RatingBar ratingBar;
        ImageButton report;
        UserManager userManager;
        FirebaseFirestore db;

        /**
         * Constructor for the ViewHolder class, which represents each item in the RecyclerView.
         * Also set up the database for loading data use.
         * @param itemView The root view of the item layout.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewerName = itemView.findViewById(R.id.reviewerName);
            date = itemView.findViewById(R.id.reviewDate);
            ratingBar = itemView.findViewById(R.id.star);
            description = itemView.findViewById(R.id.reviewDescription);
            reviewLike = itemView.findViewById(R.id.reviewLikeBtn);
            likeNumber = itemView.findViewById(R.id.reviewLikeNum);
            reviewDislike = itemView.findViewById(R.id.reviewDislikeBtn);
            dislikeNumber = itemView.findViewById(R.id.reviewDislikeNum);
            report = itemView.findViewById(R.id.report_review_btn);

            db = FirebaseFirestore.getInstance();

            userManager = new UserManager(FirebaseFirestore.getInstance(), "users");

            // Set up the reviewManager for loading data from the database of review collection.
            reviewManager = new ReviewManager(FirebaseFirestore.getInstance(),
                    studyUnit.getReviewCollectionName(),
                    studyUnit.getReviewLikeCollectionName(),
                    studyUnit.getReviewDislikeCollectionName(),
                    studyUnit.getReviewCommentCollectionName());
        }

        /**
         * Binds the data from a Review object to the views in the ViewHolder.
         * Also sets up click listeners for various interactions as like, dislike, and reporting a review.
         * @param review The Review object containing the data to be displayed.
         */
        public void bind(Review review) {

            // Set publisher name
            userManager.downloadOne(review.getPublisherId(), new ItemDownloadCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    reviewerName.setText(user.getFullName());
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Error getting the user", e);
                }});

            // Set review text
            description.setText(review.getText());
            // Set publication date
            date.setText(TimeUtils.getTimeAgo(review.getDatetime()));
            // Set like number
            likeNumber.setText(String.valueOf(review.getLikeNumber()));
            // Set dislike number
            dislikeNumber.setText(String.valueOf(review.getDislikeNumber()));

            currentUid = Objects.requireNonNull(FirebaseAuth.getInstance()
                    .getCurrentUser()).getUid();

            // Set report button for report inappropriate reviews
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    General.reportOperation(itemView.getContext(), General.COURSEREVIEWCOLLECTION, review.getId());
                }
            });

            // Set the dynamics of likes and dislikes of a review
            // The difference with a click listener is this is to check the user's like history in the database.
            reviewManager.isLiked(review.getId(), currentUid, new ItemLikeCallback() {
                @Override
                public void onSuccess(Boolean isLiked) {
                    if (!isLiked) {
                        // If it is not liked by the user, it sets to like_icon(empty)
                        // And the dislike button and number can be seen.

                        reviewLike.setImageResource(R.drawable.like_icon);
                        reviewDislike.setVisibility(View.VISIBLE);
                        dislikeNumber.setVisibility(View.VISIBLE);
                    } else {
                        // If it is liked by the user, it sets to liked_icon(filled)
                        // And the dislike button is set to unable to click on.

                        reviewLike.setImageResource(R.drawable.liked_icon);
                        likeNumber.setVisibility(View.VISIBLE);
                        reviewDislike.setEnabled(false);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                }
            });

            // Set up the click listener for the like button to like a review
            reviewLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reviewManager.likeOrUnlike(review, currentUid, new ItemLikeCallback() {
                        @Override
                        public void onSuccess(Boolean isLiked) {
                            if (!isLiked) {
                                reviewLike.setImageResource(R.drawable.like_icon);
                                reviewLike.setEnabled(true);
                                reviewDislike.setEnabled(true);
                                reviewDislike.setVisibility(View.VISIBLE);
                                dislikeNumber.setVisibility(View.VISIBLE);
                            } else {
                                reviewLike.setImageResource(R.drawable.liked_icon);
                                likeNumber.setVisibility(View.VISIBLE);
                                reviewDislike.setEnabled(false);
                            }
                            // Set the number of likes to be present after clicking or de-liked.
                            likeNumber.setText(String.valueOf(review.getLikeNumber()));
                        }

                        @Override
                        public void onFailure(Exception e) {}
                    });
                }
            });

            // Set up the dynamics of likes and dislikes of a review
            reviewManager.isDisliked(review.getId(), currentUid, new ItemLikeCallback() {
                @Override
                public void onSuccess(Boolean isDisliked) {
                    if (!isDisliked) {
                        // If it is not disliked by the user, it sets to dislike_empty
                        // And the like button and number can be seen.

                        reviewDislike.setImageResource(R.drawable.dislike_empty);
                        reviewLike.setVisibility(View.VISIBLE);
                        likeNumber.setVisibility(View.VISIBLE);
                    } else {
                        // If it is disliked by the user, it sets to dislike_filled
                        // And the like button is set to unable to click on.

                        reviewDislike.setImageResource(R.drawable.dislike_filled);
                        dislikeNumber.setVisibility(View.VISIBLE);
                        reviewLike.setEnabled(false);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                }
            });

            // Set up the click listener for the dislike button to dislike a review
            reviewDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reviewManager.dislikeOrUndislike(review, currentUid, new ItemLikeCallback() {
                        @Override
                        public void onSuccess(Boolean isDisliked) {
                            if (!isDisliked) {
                                reviewDislike.setImageResource(R.drawable.dislike_empty);
                                reviewLike.setEnabled(true);
                                reviewDislike.setEnabled(true);
                                reviewLike.setVisibility(View.VISIBLE);
                                likeNumber.setVisibility(View.VISIBLE);
                            } else {
                                reviewDislike.setImageResource(R.drawable.dislike_filled);
                                dislikeNumber.setVisibility(View.VISIBLE);
                                reviewLike.setEnabled(false);
                            }
                            // Set the number of dislikes to be present after clicking or de-disliked.
                            dislikeNumber.setText(String.valueOf(review.getDislikeNumber()));
                        }

                        @Override
                        public void onFailure(Exception e) {}
                    });
                }
            });
        }

    }
}
