package com.example.connectue.fragments;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.R;
import com.example.connectue.activities.MainActivity;
import com.example.connectue.adapters.ReplyAdapter;
import com.example.connectue.databinding.FragmentQuestionsBinding;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.managers.QuestionManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.Comment;
import com.example.connectue.model.Question;
import com.example.connectue.model.User;
import com.example.connectue.utils.TimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for the question page with the question showed and add a reply button.
 */
public class ReplyFragment extends Fragment{

    private TextView publisherName;
    private TextView publisherTime;
    private TextView replyQuestionDescription;
    private EditText addReply;
    private ImageButton sendReplyBtn;
    private ImageButton backBtn;
    private ReplyAdapter replyAdapter;

    private RecyclerView commentsRecyclerView;
    private List<Comment> commentList;

    private Question currentQuestion;

    private String questionId;

    private int repliesPerChunk = 10;

    private QuestionManager questionManager;
    private UserManager userManager;

    /**
     * Class tag for logs.
     */
    private static final String TAG = "ReplyFragment";

    private FragmentQuestionsBinding binding;

    /**
     * Indicates if posts are currently loading from database.
     */
    private Boolean isLoading = false;

    /**
     * Called when the fragment is created. Initializes questionManager and userManager.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionManager = new QuestionManager(FirebaseFirestore.getInstance(),
                Question.QUESTION_COLLECTION_NAME, Question.QUESTION_LIKE_COLLECTION_NAME,
                Question.QUESTION_DISLIKE_COLLECTION_NAME, Question.QUESTION_COMMENT_COLLECTION_NAME);
        userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
    }

    /**
     * Inflates the layout for the fragment, initializes views, retrieves questionId from arguments,
     * initializes RecyclerView and its adapter, loads question and user data, displays comments, and sets up
     * functionality for uploading replies and downloading more comments on scrolling.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return The inflated view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reply, container, false);

        // Setup views
        initView(view);

        // Get current questionId
        Bundle bundle = getArguments();
        assert bundle != null;
        questionId = bundle.getString("questionId");

        commentList = new ArrayList<>();
        replyAdapter = new ReplyAdapter(getContext(), commentList);

        // Initialize comments RecyclerView
        initRecyclerView(view);

        // Load question and user data from database, display in to UI.
        setQuestionInfo();

        // Load and display first chunk of comments
        loadComments();

        // Upload comment function
        initCreateReplies();

        // Sets on scroll listener to download more comments.
        setCommentOnScroll(commentsRecyclerView);

        return view;
    }

    private void initView(View view) {
        publisherName = view.findViewById(R.id.reply_questioner_Name);
        publisherTime = view.findViewById(R.id.reply_question_time);
        replyQuestionDescription = view.findViewById(R.id.reply_question_description);
        addReply = view.findViewById(R.id.addReplyET);
        sendReplyBtn = view.findViewById(R.id.reply_send_Btn);
        backBtn = view.findViewById(R.id.backFromReplyBtn);

    }

    private void initRecyclerView(View view) {
        commentsRecyclerView = view.findViewById(R.id.replyRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentsRecyclerView.setHasFixedSize(false);
        commentsRecyclerView.setAdapter(replyAdapter);
    }

    /**
     * Retrieves question information from the database based on the provided questionId,
     * sets the question text, publication time, and publisher name in the UI, and adds a click
     * listener to the back button to navigate back to the MainActivity.
     */
    private void setQuestionInfo() {
        questionManager.downloadOne(questionId, new ItemDownloadCallback<Question>() {
            @Override
            public void onSuccess(Question question) {
                replyQuestionDescription.setText(question.getText());
                currentQuestion = question;
                publisherTime.setText(TimeUtils.getTimeAgo(question.getDatetime()));

                userManager.downloadOne(question.getPublisherId(), new ItemDownloadCallback<User>() {
                    @Override
                    public void onSuccess(User publisher) {
                        publisherName.setText(publisher.getFullName());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error getting user", e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error getting post", e);
            }
        });

        // Back to questionFragment
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Loads a chunk of comments associated with the current question from the database.
     * This method downloads recent comments based on the provided questionId and adds them
     * to the commentList. It also notifies the replyAdapter of the dataset change and sets
     * isLoading to false upon successful download.
     */
    public void loadComments() {
        questionManager.downloadRecentComments(questionId, repliesPerChunk,
                    new ItemDownloadCallback<List<Comment>>() {

                @Override
                public void onSuccess(List<Comment> comments) {
                    commentList.addAll(comments);
                    // Notify the adapter of the dataset change
                    replyAdapter.notifyDataSetChanged();
                    isLoading = false;
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Error while downloading comments", e);
                }
                });
    }

    /**
     * Initializes the functionality for creating replies.
     */
    private void initCreateReplies() {
        sendReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replyText = addReply.getText().toString();
                // Check if the reply is not empty
                if (!TextUtils.isEmpty(replyText)) {
                    // Call a method to upload the reply to Firestore
                    uploadRepliesToFirestore(replyText);
                    // Clear the EditText after submitting the reply
                    addReply.setText("");
                } else {
                    // Show an error message or toast if the reply is empty
                    Toast.makeText(getContext(), "Please enter a reply", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * Uploads a reply to Firestore.
     *
     * @param replyText The text of the reply to be uploaded to Firestore.
     */
    private void uploadRepliesToFirestore(String replyText) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Comment comment = new Comment(userId, replyText, questionId);
        questionManager.uploadComment(comment, currentQuestion, new ItemUploadCallback() {
            @Override
            public void onSuccess() {
                // Update the UI to reflect the new reply

                commentList.add(0, comment);
                // Notify the RecyclerView adapter about the dataset change
                replyAdapter.notifyItemInserted(0);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error adding question", e);
            }
        });

    }

    /**
     * Sets a scroll listener on the comments RecyclerView to trigger the loading of more comments
     * when the end of the list is reached.
     *
     * @param commentsRecyclerView The RecyclerView containing the comments.
     */
    private void setCommentOnScroll(RecyclerView commentsRecyclerView) {
        commentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) commentsRecyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Check if end of the list is reached
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    Log.e(TAG, String.format("%d %d %d", visibleItemCount,
                            firstVisibleItemPosition, totalItemCount));
                    // Assuming PAGE_SIZE is the number of items to load per page
                    // Load more items
                    isLoading = true;
                    loadComments();
                }
            }
        });
    }
}
