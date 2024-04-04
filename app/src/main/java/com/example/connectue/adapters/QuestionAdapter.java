package com.example.connectue.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.connectue.R;
import com.example.connectue.fragments.ReplyFragment;
import com.example.connectue.managers.UserManager;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.model.Question;
import com.example.connectue.model.User;
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

    /**
     * Constructor of a question adapter with the provided data.
     * @param questionList The list of questions to be displayed
     * @param fragmentManager The fragment manager for handling fragments within the adapter.
     */
    public QuestionAdapter(List<Question> questionList, FragmentManager fragmentManager) {
        this.questionList = questionList;
        this.fragmentManager = fragmentManager;
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
    public QuestionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_questions, parent, false);
        return new QuestionAdapter.MyViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the ViewHolder to reflect the item at the given position.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.MyViewHolder holder, int position) {
        // assign values to the views we created in the row_questions file
        // based on the position of the recycler view
        Question question = questionList.get(position);
        holder.bind(question);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter's data set.
     */
    @Override
    public int getItemCount() {
        return questionList.size();
    }

    /**
     * The public class to grab the view contents, bind the views, and set the interaction.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView questionLike, replyQuestionBtn;
        TextView publisherName, description, likeNumber, date, replyQuestionNum;
        UserManager userManager;


        /**
         * Constructor for the ViewHolder class, which represents each item in the RecyclerView.
         * Also set up the database for loading data use.
         * @param itemView The root view of the item layout.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            publisherName = itemView.findViewById(R.id.questionerName);
            date = itemView.findViewById(R.id.questionDate);
            description = itemView.findViewById(R.id.question);
            questionLike = itemView.findViewById(R.id.questionLike);
            likeNumber = itemView.findViewById(R.id.questionLikeNum);
            replyQuestionBtn = itemView.findViewById(R.id.replyQuestionBtn);
            replyQuestionNum = itemView.findViewById(R.id.replyQuestionNum);

            userManager = new UserManager(FirebaseFirestore.getInstance(), "users");

        }

        /**
         * Binds the data from a Question object to the views in the ViewHolder.
         * Also sets up click listeners for various interactions as like, reply to a question.
         * @param question The Question object containing the data to be displayed.
         */
        public void bind(Question question) {

            // Set publisher name
            userManager.downloadOne(question.getPublisherId(), new ItemDownloadCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    publisherName.setText(user.getFullName());
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Error getting the user", e);
                }});

            // Set main text
            description.setText(question.getText());
            // Set publication date
            date.setText(TimeUtils.getTimeAgo(question.getDatetime()));
            // Set like number
            likeNumber.setText(String.valueOf(question.getLikeNumber()));
            // Set reply number
            replyQuestionNum.setText(String.valueOf(question.getCommentNumber()));

            // Set the click listener of like a question.
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

            // load from the questions collection of the database.
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

            // Set the click listener of replying to a question button.
            replyQuestionBtn.setOnClickListener(v -> {
                Log.d(TAG, "onClick: card clicked");
                navigateToQuestionsFragment(question.getId());
            });

        }

        /**
         * Method is called when clicking on a reply button to reply to a question.
         * After it will be redirected to the QuestionsFragment.
         * @param questionId The Id to be checked foo referring to specific question.
         */
        private void navigateToQuestionsFragment(String questionId) {

            // Create a new instance of the ReplyFragment
            ReplyFragment replyFragment = new ReplyFragment();
            // Create a bundle to pass data to the fragment
            Bundle bundle = new Bundle();
            bundle.putString("questionId", questionId);
            // Set the arguments bundle to the fragment
            replyFragment.setArguments(bundle);

            // Begin a new fragment transaction
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // Replace the current fragment with the replyFragment
            transaction.replace(R.id.frame_layout, replyFragment);
            // Add the transaction to the back stack
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();


        }
    }
}
