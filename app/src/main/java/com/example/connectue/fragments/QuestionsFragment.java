package com.example.connectue.fragments;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.R;
import com.example.connectue.activities.AddQuestionActivity;
import com.example.connectue.activities.CourseViewActivity;
import com.example.connectue.adapters.QuestionAdapter;
import com.example.connectue.databinding.FragmentQuestionsBinding;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.QuestionManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.model.Question;
import com.example.connectue.model.User2;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for the home page with the post feed and add a post button.
 */
public class QuestionsFragment extends Fragment {

    /**
     * Class tag for logs.
     */
    private static final String tag = "QuestionsFragment";

    private FragmentQuestionsBinding binding;

    /**
     * Adapter that is responsible for outputting posts to UI.
     */
    private QuestionAdapter questionAdapter;

    /**
     * Manager for database requests for posts collection.
     */
    private QuestionManager questionManager;

    /**
     * Indicates if posts are currently loading from database.
     */
    private Boolean isLoading = false;

    /**
     * Indicates if posts are currently loading from database.
     */
    private FragmentManager fragmentManager;

    /**
     * Course model of the opened page.
     */
    StudyUnit course;

    public QuestionsFragment() {
        // Default constructor
    }

    public QuestionsFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentQuestionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Define reviews recycler view.
        RecyclerView questionsRecyclerView = binding.recyclerViewQuestion;

        // Initialize database post manager.
        questionManager = new QuestionManager(FirebaseFirestore.getInstance(),
                Question.QUESTION_COLLECTION_NAME, Question.QUESTION_LIKE_COLLECTION_NAME,
                Question.QUESTION_DISLIKE_COLLECTION_NAME, Question.QUESTION_COMMENT_COLLECTION_NAME);

        // Initialize course id.
        course = retrieveCourse();

        // Initializing the list of posts in the feed.
        List<Question> questionList = new ArrayList<>();

        //Initialize RecyclerView
        initRecyclerView(questionList, questionsRecyclerView);

        // Upload from database and display first chunk of posts
        loadQuestions(questionList);

        // Display the createPostBtn only for verified users
        displayAddQuestionButton(root);

        // Add a scroll listener to the RecyclerView
        getPostsOnScroll(questionList, questionsRecyclerView);

        return root;
    }

    private void initRecyclerView(List<Question> questionList, RecyclerView questionsRecyclerview) {
        questionAdapter = new QuestionAdapter(questionList, fragmentManager);
        questionsRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        questionsRecyclerview.setHasFixedSize(false);
        questionsRecyclerview.setAdapter(questionAdapter);
    }

    private void loadQuestions(List<Question> questionList) {
        int questionsPerChunk = 4;

        questionManager.downloadRecent(course.getId(), questionsPerChunk, new ItemDownloadCallback<List<Question>>() {
            @Override
            public void onSuccess(List<Question> data) {
                questionList.addAll(data);
                questionAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Error while downloading question", e);
            }
        });
    }

    private void displayAddQuestionButton(View root) {
        UserManager userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ExtendedFloatingActionButton addQuestionButton = root.findViewById(R.id.addQuestionBtn);
        userManager.downloadOne(currentUid, new ItemDownloadCallback<User2>() {
            @Override
            public void onSuccess(User2 data) {
                if (data.isVerified()) {
                    addQuestionButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), AddQuestionActivity.class);
                        intent.putExtra("course", course.studyUnitToString());
                        startActivity(intent);
                    });
                } else {
                    addQuestionButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Error while retrieving user from the database", e);
            }
        });
    }

    private void getPostsOnScroll(List<Question> questionList, RecyclerView questionsRecyclerView) {
        questionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) questionsRecyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Check if end of the list is reached
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    // Assuming PAGE_SIZE is the number of items to load per page
                    // Load more items
                    isLoading = true;
                    loadQuestions(questionList);
                }
            }
        });
    }

    /**
     * Retrieves the course model of current page.
     */
    private StudyUnit retrieveCourse() {
        CourseViewActivity courseViewActivity = (CourseViewActivity) getActivity();
        if (courseViewActivity != null) {
            return courseViewActivity.getCourse();
        } else {
            return new StudyUnit("0", "0", StudyUnit.StudyUnitType.COURSE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}