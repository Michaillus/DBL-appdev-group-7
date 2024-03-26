package com.example.connectue.adapters;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.connectue.R;
import com.example.connectue.managers.UserManager;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.model.Question;
import com.example.connectue.model.User2;
import com.example.connectue.utils.TimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "QuestionAdapter";

    List<Question> questionList;

    private FirebaseFirestore db;
    private String currentUid;
    private FragmentManager fragmentManager;

    public QuestionAdapter(List<Question> questionList, FragmentManager fragmentManager) {
        this.questionList = questionList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public QuestionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_question_row, parent, false);
        return new QuestionAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.MyViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView questionLike;
        TextView publisherName, description, likeNumber, date;
        UserManager userManager;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            publisherName = itemView.findViewById(R.id.questionerName);
            date = itemView.findViewById(R.id.questionDate);
            description = itemView.findViewById(R.id.question);
            questionLike = itemView.findViewById(R.id.questionLike);
            likeNumber = itemView.findViewById(R.id.questionLikeNum);

            userManager = new UserManager(FirebaseFirestore.getInstance(), "users");

        }

        public void bind(Question question) {

            // Set publisher name
            userManager.downloadOne(question.getPublisherId(), new FireStoreDownloadCallback<User2>() {
                @Override
                public void onSuccess(User2 user) {
                    publisherName.setText(user.getFullName());
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Error getting the user", e);
                }});

            // Set question text
            description.setText(question.getText());
            // Set publication date
            date.setText(TimeUtils.getTimeAgo(question.getDatetime()));
            // Set like number
            likeNumber.setText(String.valueOf(question.getLikeNumber()));

            questionLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long currentLikes = question.getLikeNumber();
                    question.setLikeNumber(currentLikes + 1);
                    notifyDataSetChanged(); // Refresh UI to reflect changes
                }
            });

            db = FirebaseFirestore.getInstance();
            DocumentReference questionRef = db.collection("questions").document(question.getId());

            questionRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot questionDocument = task.getResult();

                    // Check whether the review has "likedByUsers" field
                    if (!questionDocument.contains("likedByUsers")) {
                        questionRef.update("likedByUsers", new ArrayList<String>())
                                .addOnSuccessListener(aVoid -> {
                                    // Field "likedByUsers" added successfully

                                })
                                .addOnFailureListener(e -> {
                                    // Error adding field "likedByUsers"
                                });
                    }


                    // If likedByUsers exists, check whether this post is liked by current user
                    if (questionDocument.contains("likedByUsers")) {
                        List<String> likedByUsers = (List<String>) questionDocument.get("likedByUsers");
                        currentUid = Objects.requireNonNull(FirebaseAuth.getInstance()
                                .getCurrentUser()).getUid();

                        // Display like states of the like button
                        if (!likedByUsers.contains(currentUid)) {
                            questionLike.setImageResource(R.drawable.like_icon);
                        } else {
                            questionLike.setImageResource(R.drawable.liked_icon);
                        }

                        // If the user presses the like button, the post question is liked or disliked
                        questionLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!likedByUsers.contains(currentUid)) {
                                    likedByUsers.add(currentUid);
                                    questionLike.setImageResource(R.drawable.liked_icon);
                                    question.incrementLikeNumber();
                                    likeNumber.setText(String.valueOf(question.getLikeNumber()));
                                } else {
                                    questionLike.setImageResource(R.drawable.like_icon);
                                    likedByUsers.remove(currentUid);
                                    question.decrementLikeNumber();
                                    likeNumber.setText(String.valueOf(question.getLikeNumber()));
                                }
                                questionRef.update("likedByUsers", likedByUsers);
                                questionRef.update("likes", question.getLikeNumber());
                            }
                        });
                    }

                }
            });
        }
    }
}
