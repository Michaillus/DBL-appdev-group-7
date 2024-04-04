package com.example.connectue.fragments;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.R;
import com.example.connectue.activities.AddQuestionActivity;
import com.example.connectue.activities.CourseViewActivity;
import com.example.connectue.activities.StudyUnitViewActivity;
import com.example.connectue.adapters.QuestionAdapter;
import com.example.connectue.databinding.FragmentQuestionsBinding;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.QuestionManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.model.Question;
import com.example.connectue.model.User;
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
    private static final String TAG = "QuestionsFragment";

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

    /**
     * Activity result launcher for launching the reload method on finish add question activity.
     */
    ActivityResultLauncher<Intent> activityResultLauncher;

    /**
     * Default constructor for this class.
     */
    public QuestionsFragment() {
        // Default constructor
    }

    /**
     * Constructor of a question fragment
     * @param fragmentManager The fragment manager for handling fragments within this fragment.
     */
    public QuestionsFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * Called to have the fragment instantiate its UI view of question page.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the view for the fragment's UI of the question page of s specific course, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentQuestionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Defines listener for reloading the study unit view activity when user returns from
        // add question activity page.
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.e(TAG, "Does this work?");
                    StudyUnitViewActivity studyUnitViewActivity = (StudyUnitViewActivity) getActivity();
                    if (studyUnitViewActivity != null) {
                        studyUnitViewActivity.reload();
                    } else {
                        Log.e(TAG, "Unable to get Study unit view activity");
                    }

                });

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

    /**
     * Initializes the recyclerView with the provided list of questions and sets up the adapter for views.
     * @param questionList The list of questions to be displayed in the recyclerView.
     * @param questionsRecyclerview The recyclerView instance to be initialized.
     */
    private void initRecyclerView(List<Question> questionList, RecyclerView questionsRecyclerview) {
        // Create a new instance of QuestionAdapter and set it as the adapter for the RecyclerView
        questionAdapter = new QuestionAdapter(questionList, fragmentManager);
        // Set layout manager for RecyclerView
        questionsRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Set whether the size of the RecyclerView will be affected by the adapter contents
        questionsRecyclerview.setHasFixedSize(false);
        // Set the adapter for the RecyclerView
        questionsRecyclerview.setAdapter(questionAdapter);
    }

    /**
     * Loads questions from the database and adds them to the provided question list.
     * This method is called to fetch a chunk of recent questions and update the RecyclerView.
     * @param questionList The list of questions to which the fetched questions will be added.
     */
    private void loadQuestions(List<Question> questionList) {
        int questionsPerChunk = 4;

        // Download recent questions from the database
        questionManager.downloadRecent(course.getId(), questionsPerChunk, new ItemDownloadCallback<List<Question>>() {
            @Override
            public void onSuccess(List<Question> data) {
                questionList.addAll(data);

                // Notify the adapter that the data set has changed
                questionAdapter.notifyDataSetChanged();
                // Set isLoading to false indicating that the data loading process is complete
                isLoading = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while downloading question", e);
            }
        });
    }

    /**
     * Displays the "Add Question" button.
     * @param root The view for checking the View object and use it it to find specific view to control.
     */
    private void displayAddQuestionButton(View root) {
        UserManager userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ExtendedFloatingActionButton addQuestionButton = root.findViewById(R.id.addQuestionBtn);
        userManager.downloadOne(currentUid, new ItemDownloadCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.getRole() == User.STUDENT_USER_ROLE || user.getRole() == User.ADMIT_USER_ROLE) {
                    Log.i(TAG, "User is allowed to add a question");
                    addQuestionButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), AddQuestionActivity.class);
                        intent.putExtra("course", course.studyUnitToString());

                        activityResultLauncher.launch(intent);
                    });
                    addQuestionButton.setVisibility(View.VISIBLE);
                } else {
                    Log.i(TAG, "User is not allowed to add a question");
                    addQuestionButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while retrieving user from the database", e);
            }
        });
    }

    /**
     * Sets up a scroll listener for the RecyclerView to detect when the user has scrolled to
     * the end of the list and triggers loading more questions.
     * @param questionList The list of questions being displayed in the recyclerView.
     * @param questionsRecyclerView The RecyclerView containing the questions.
     */
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
            return courseViewActivity.getStudyUnit();
        } else {
            return new StudyUnit("0", "0", StudyUnit.StudyUnitType.COURSE);
        }
    }

    /**
     * Called when the fragment's view is being destroyed. It releases any resources associated with the view.
     * This method should be overridden to clean up references to the view and prevent memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}