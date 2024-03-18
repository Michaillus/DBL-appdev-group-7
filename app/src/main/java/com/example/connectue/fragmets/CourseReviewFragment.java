package com.example.connectue.fragmets;

import androidx.appcompat.app.AppCompatActivity;

import com.example.connectue.R;
import com.example.connectue.model.Review;

import java.util.ArrayList;

public class CourseReviewFragment extends AppCompatActivity {
    ArrayList<Review> reviewModels = new ArrayList<>();
    int[] likeImages = {R.drawable.like_icon};
    int[] dislikeImages = {R.drawable.dislike};
    int[] starsImages = {R.id.star};

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_course_view);
//
//        RecyclerView recyclerView = findViewById(R.id.recyclerView_review);
//
//        setUpCourseReviewFragmentModels();
//
//        AdapterReviews adapter = new AdapterReviews(this, reviewModels);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }

    private void setUpCourseReviewFragmentModels() {
        String [] uName = getResources().getStringArray(R.array.reviewerName);
        String [] uText = getResources().getStringArray(R.array.reviews);
        String [] date = getResources().getStringArray(R.array.date);
        String [] likeNums = getResources().getStringArray(R.array.likeNum);
        String [] dislikeNums = getResources().getStringArray(R.array.dislikeNum);

        for (int i = 0; i<uName.length; i++) {
            reviewModels.add(new Review(uName[i], uText[i], date[i], likeImages[i], dislikeImages[i], starsImages[i], likeNums[i], dislikeNums[i]));
        }
    }
}
