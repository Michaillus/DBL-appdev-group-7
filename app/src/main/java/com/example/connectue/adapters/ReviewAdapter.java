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
import com.example.connectue.managers.CourseReviewManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.model.Course;
import com.example.connectue.model.CourseReview;
import com.example.connectue.model.User2;
import com.example.connectue.utils.TimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    List<CourseReview> courseReviewList;
    private String currentUid;
    private String TAG = "TestAdapterReviews";
    private FragmentManager fragmentManager;
    private CourseReviewManager courseReviewManager;

    /**
     * Study unit of the current page.
     */
    private final Course course;

    public ReviewAdapter(List<CourseReview> courseReviewList, FragmentManager fragmentManager,
                         Course course) {
        this.courseReviewList = courseReviewList;
        this.fragmentManager = fragmentManager;
        this.course = course;
    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (Giving a look to our rows)

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.course_review_row, parent, false);
        return new ReviewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MyViewHolder holder, int position) {

        // assign values to the views we created in the course_review_row file
        // based on the position of the recycler view
        CourseReview courseReview = courseReviewList.get(position);
        holder.bind(courseReview);
        holder.ratingBar.setIsIndicator(true);
        holder.ratingBar.setRating(courseReview.getStars());

    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items you want to display
        return courseReviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // grab the views from course_review_row file
        private ImageView reviewLike, reviewDislike;
        private TextView reviewerName, description, date;
        private TextView likeNumber, dislikeNumber;
        private RatingBar ratingBar;
        UserManager userManager;

        private ImageButton[] reviewStars;
        private long currentRating;
        private CourseReview currentCourseReview;
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
            courseReviewManager = new CourseReviewManager(FirebaseFirestore.getInstance(),
                    course.getReviewCollectionName(),
                    course.getReviewLikeCollectionName(),
                    course.getReviewDislikeCollectionName(),
                    course.getReviewCommentCollectionName());
        }

        public void bind(CourseReview courseReview) {

            // Set publisher name
            userManager.downloadOne(courseReview.getPublisherId(), new ItemDownloadCallback<User2>() {
                @Override
                public void onSuccess(User2 user) {
                    reviewerName.setText(user.getFullName());
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Error getting the user", e);
                }});

            // Set review text
            description.setText(courseReview.getText());
            // Set publication date
            date.setText(TimeUtils.getTimeAgo(courseReview.getDatetime()));
            // Set like number
            likeNumber.setText(String.valueOf(courseReview.getLikeNumber()));
            // Set dislike number
            dislikeNumber.setText(String.valueOf(courseReview.getDislikeNumber()));

            currentUid = Objects.requireNonNull(FirebaseAuth.getInstance()
                    .getCurrentUser()).getUid();



            courseReviewManager.isLiked(courseReview.getId(), currentUid, new ItemLikeCallback() {
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
                    courseReviewManager.likeOrUnlike(courseReview, currentUid, new ItemLikeCallback() {
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
                            likeNumber.setText(String.valueOf(courseReview.getLikeNumber()));
                        }

                        @Override
                        public void onFailure(Exception e) {}
                    });
                }
            });



            courseReviewManager.isDisliked(courseReview.getId(), currentUid, new ItemLikeCallback() {
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
                    courseReviewManager.dislikeOrUndislike(courseReview, currentUid, new ItemLikeCallback() {
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
                            dislikeNumber.setText(String.valueOf(courseReview.getDislikeNumber()));
                        }

                        @Override
                        public void onFailure(Exception e) {}
                    });
                }
            });
        }

    }
}
