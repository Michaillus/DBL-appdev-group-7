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
import com.example.connectue.model.User2;
import com.example.connectue.utils.TimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    List<Review> reviewList;
    private String currentUid;
    private String TAG = "TestAdapterReviews";
    private FragmentManager fragmentManager;
    private ReviewManager reviewManager;

    /**
     * Study unit of the current page.
     */
    private final StudyUnit studyUnit;

    public ReviewAdapter(List<Review> reviewList, FragmentManager fragmentManager,
                         StudyUnit studyUnit) {
        this.reviewList = reviewList;
        this.fragmentManager = fragmentManager;
        this.studyUnit = studyUnit;
    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (Giving a look to our rows)

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.study_unit_review_row, parent, false);
        return new ReviewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MyViewHolder holder, int position) {

        // assign values to the views we created in the study_unit_review_row file
        // based on the position of the recycler view
        Review review = reviewList.get(position);
        holder.bind(review);
        holder.ratingBar.setIsIndicator(true);
        holder.ratingBar.setRating(review.getStars());

    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items you want to display
        return reviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // grab the views from study_unit_review_row file
        private ImageView reviewLike, reviewDislike;
        private TextView reviewerName, description, date;
        private TextView likeNumber, dislikeNumber;
        private RatingBar ratingBar;
        UserManager userManager;

        private ImageButton[] reviewStars;
        private long currentRating;
        private Review currentReview;
        private FirebaseFirestore db;

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

            db = FirebaseFirestore.getInstance();

            userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
            reviewManager = new ReviewManager(FirebaseFirestore.getInstance(),
                    studyUnit.getReviewCollectionName(),
                    studyUnit.getReviewLikeCollectionName(),
                    studyUnit.getReviewDislikeCollectionName(),
                    studyUnit.getReviewCommentCollectionName());
        }

        public void bind(Review review) {

            // Set publisher name
            userManager.downloadOne(review.getPublisherId(), new ItemDownloadCallback<User2>() {
                @Override
                public void onSuccess(User2 user) {
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



            reviewManager.isLiked(review.getId(), currentUid, new ItemLikeCallback() {
                @Override
                public void onSuccess(Boolean isLiked) {
                    if (!isLiked) {
                        reviewLike.setImageResource(R.drawable.like_icon);
//                        reviewLike.setEnabled(true);
//                        reviewDislike.setEnabled(true);
                        reviewDislike.setVisibility(View.VISIBLE);
                        dislikeNumber.setVisibility(View.VISIBLE);
                    } else {
                        reviewLike.setImageResource(R.drawable.liked_icon);
                        likeNumber.setVisibility(View.VISIBLE);
                        reviewDislike.setEnabled(false);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                }
            });

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
                            likeNumber.setText(String.valueOf(review.getLikeNumber()));
                        }

                        @Override
                        public void onFailure(Exception e) {}
                    });
                }
            });



            reviewManager.isDisliked(review.getId(), currentUid, new ItemLikeCallback() {
                @Override
                public void onSuccess(Boolean isDisliked) {
                    if (!isDisliked) {
                        reviewDislike.setImageResource(R.drawable.dislike_empty);
//                        reviewLike.setEnabled(true);
//                        reviewDislike.setEnabled(true);
                        reviewLike.setVisibility(View.VISIBLE);
                        likeNumber.setVisibility(View.VISIBLE);
                    } else {
                        reviewDislike.setImageResource(R.drawable.dislike_filled);
                        dislikeNumber.setVisibility(View.VISIBLE);
                        reviewLike.setEnabled(false);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                }
            });

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
