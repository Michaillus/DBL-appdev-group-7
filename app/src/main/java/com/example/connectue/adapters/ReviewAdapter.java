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
import com.example.connectue.interfaces.FireStoreLikeCallback;
import com.example.connectue.managers.ReviewManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.model.Review;
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


    public ReviewAdapter(List<Review> reviewList, FragmentManager fragmentManager) {
        this.reviewList = reviewList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (Giving a look to our rows)

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_review_row, parent, false);
        return new ReviewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MyViewHolder holder, int position) {
        // assign values to the views we created in the course_review_row file
        // based on the position of the recycler view
        Review review = reviewList.get(position);

        holder.userManager.downloadOne(review.getPublisherId(),
                new FireStoreDownloadCallback<User2>() {
            @Override
            public void onSuccess(User2 user) {
                holder.reviewerName.setText(user.getFullName());
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error getting the user", e);
            }
        });
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

        // grab the views from course_review_row file
        private ImageView reviewLike, reviewDislike;
        private TextView reviewerName, reviewDescription, reviewDate;
        private TextView reviewLikeNum, reviewDislikeNum;
        private RatingBar ratingBar;
        UserManager userManager;

        private ImageButton[] reviewStars;
        private long currentRating;
        private Review currentReview;
        private FirebaseFirestore db;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewerName = itemView.findViewById(R.id.reviewerName);
            reviewDate = itemView.findViewById(R.id.reviewDate);
            ratingBar = itemView.findViewById(R.id.star);
            reviewDescription = itemView.findViewById(R.id.questionDescription);
            reviewLike = itemView.findViewById(R.id.reviewLikeBtn);
            reviewLikeNum = itemView.findViewById(R.id.reviewLikeNum);
            reviewDislike = itemView.findViewById(R.id.reviewDislikeBtn);
            reviewDislikeNum = itemView.findViewById(R.id.reviewDislikeNum);

            db = FirebaseFirestore.getInstance();

            userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
            reviewManager = new ReviewManager(FirebaseFirestore.getInstance(), Review.REVIEW_COLLECTION_NAME,
                    Review.REVIEW_LIKE_COLLECTION_NAME, Review.REVIEW_DISLIKE_COLLECTION_NAME,
                    Review.REVIEW_COMMENT_COLLECTION_NAME);

        }

        public void bind(Review review) {

            reviewDescription.setText(review.getText());
            reviewDate.setText(TimeUtils.getTimeAgo(review.getDatetime()));
            reviewLikeNum.setText(String.valueOf(review.getLikeNumber()));
            reviewDislikeNum.setText(String.valueOf(review.getDislikeNumber()));

            currentUid = Objects.requireNonNull(FirebaseAuth.getInstance()
                    .getCurrentUser()).getUid();



            reviewManager.isLiked(review.getId(), currentUid, new FireStoreLikeCallback() {
                @Override
                public void onSuccess(Boolean isLiked) {
                    if (!isLiked) {
                        reviewLike.setImageResource(R.drawable.like_icon);
                    } else {
                        reviewLike.setImageResource(R.drawable.liked_icon);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                }
            });

            reviewLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reviewManager.likeOrUnlike(review, currentUid, new FireStoreLikeCallback() {
                        @Override
                        public void onSuccess(Boolean isLiked) {
                            if (!isLiked) {
                                reviewLike.setImageResource(R.drawable.like_icon);
                            } else {
                                reviewLike.setImageResource(R.drawable.liked_icon);
                            }
                            reviewLikeNum.setText(String.valueOf(review.getLikeNumber()));
                        }

                        @Override
                        public void onFailure(Exception e) {}
                    });
                }
            });



            reviewManager.isDisliked(review.getId(), currentUid, new FireStoreLikeCallback() {
                @Override
                public void onSuccess(Boolean isDisliked) {
                    if (!isDisliked) {
                        reviewDislike.setImageResource(R.drawable.dislike_empty);
                    } else {
                        reviewDislike.setImageResource(R.drawable.dislike_filled);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                }
            });

            reviewDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reviewManager.dislikeOrUndislike(review, currentUid, new FireStoreLikeCallback() {
                        @Override
                        public void onSuccess(Boolean isDisliked) {
                            if (!isDisliked) {
                                reviewDislike.setImageResource(R.drawable.dislike_empty);
                            } else {
                                reviewDislike.setImageResource(R.drawable.dislike_filled);
                            }
                            reviewDislikeNum.setText(String.valueOf(review.getDislikeNumber()));
                        }

                        @Override
                        public void onFailure(Exception e) {}
                    });
                }
            });
        }

    }
}
