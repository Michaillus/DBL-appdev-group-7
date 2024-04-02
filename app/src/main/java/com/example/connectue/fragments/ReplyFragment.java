package com.example.connectue.fragments;

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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.R;
import com.example.connectue.adapters.ReplyAdapter;
import com.example.connectue.activities.StudyUnitViewActivity;
import com.example.connectue.adapters.QuestionAdapter;
import com.example.connectue.databinding.FragmentQuestionsBinding;
import com.example.connectue.adapters.CommentAdapter;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.QuestionManager;
import com.example.connectue.managers.ReplyManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.Question;
import com.example.connectue.model.Reply;
import com.example.connectue.model.User2;
import com.example.connectue.utils.TimeUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment for the question page with the question showed and add a reply button.
 */
public class ReplyFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView publisherName;
    private TextView publisherTime;
    private TextView replyQuestionDescription;
    private EditText addReply;
    private ImageButton sendReplyBtn;
    private ImageButton backBtn;
    private ReplyAdapter replyAdapter;

    private RecyclerView repliesRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Reply> replyList;
    private List<Question> questionList;


    private FirebaseFirestore db;
    //DocumentReference postRef;
    private Question currentQuestion;

    private String questionId;
    private String replyId;

    private int repliesPerChunk = 50;
    private DocumentReference questionRef;
    private CollectionReference replyRef;
    private String userId;

    private QuestionManager questionManager;
    private ReplyManager replyManager;
    private UserManager userManager;

    private FragmentManager fragmentManager;
    /**
     * Class tag for logs.
     */
    private static final String TAG = "ReplyFragment";
    private FragmentQuestionsBinding binding;

    /**
     * Adapter that is responsible for outputting posts to UI.
     */
    private QuestionAdapter questionAdapter;

    public static QuestionsFragment newInstance(String param1, String param2) {

        QuestionsFragment fragment = new QuestionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionManager = new QuestionManager(FirebaseFirestore.getInstance(),
                Question.QUESTION_COLLECTION_NAME, Question.QUESTION_LIKE_COLLECTION_NAME,
                Question.QUESTION_DISLIKE_COLLECTION_NAME, Question.QUESTION_COMMENT_COLLECTION_NAME);
        userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reply, container, false);

        // Setup views
        initView(view);

        // Get current postId
        Bundle bundle = getArguments();
        assert bundle != null;
        questionId = bundle.getString("questionId");

        //questionList = new ArrayList<>();
        //questionAdapter = new QuestionAdapter(getContext(), questionList);
        replyList = new ArrayList<>();
        replyAdapter = new ReplyAdapter(getContext(), replyList);
        db = FirebaseFirestore.getInstance();
        replyRef = FirebaseFirestore.getInstance().collection("questions-replies");

        // Initialize comments RecyclerView
        initRepliesRecyclerView(view);

        // Load contents from Firestore
        loadRepliesFromFirestore();

        // Upload comment function
        initCreateReplies();

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

    private void initRepliesRecyclerView(View view) {
        repliesRecyclerView = view.findViewById(R.id.replyRecyclerView);
        repliesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        repliesRecyclerView.setAdapter(replyAdapter);
    }

    private void loadRepliesFromFirestore() {
        questionManager.downloadOne(questionId, new ItemDownloadCallback<Question>() {
            @Override
            public void onSuccess(Question question) {
                replyQuestionDescription.setText(question.getText());
                currentQuestion = question;
                publisherTime.setText(TimeUtils.getTimeAgo(question.getDatetime()));

                userManager.downloadOne(question.getPublisherId(), new ItemDownloadCallback<User2>() {
                    @Override
                    public void onSuccess(User2 publisher) {
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
        loadReplies(replyId);

        // Back to questionFragment
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StudyUnitViewActivity.class);
                startActivity(intent);
            }
        });
    }

    public void loadReplies(String questionId) {
        Log.e("test", "test");
        questionManager.downloadRecentReplies(questionId, repliesPerChunk,
                    new ItemDownloadCallback<List<Reply>>() {

                @Override
                public void onSuccess(List<Reply> replies) {
                    Log.e("test", String.valueOf(replies.size()));
                    replyList.addAll(replies);
                    replyAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Error while downloading comments", e);
                }
                });
    }

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


    private void uploadRepliesToFirestore(String replyText) {
        questionRef = db.collection("questions").document(questionId);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Date date = new Date();
        Reply reply = new Reply(replyId, userId, replyText, questionId, new Date());
        Timestamp timestamp = new Timestamp(date);
        Map<String, Object> replyData = new HashMap<>();
        replyData.put("text", replyText);
        replyData.put("parentId", questionId);
        replyData.put("timestamp", timestamp);
        replyData.put("userId", userId);
        replyRef.add(replyData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Update the UI to reflect the new reply
                        userManager.downloadOne(userId, new ItemDownloadCallback<User2>() {
                            @Override
                            public void onSuccess(User2 user) {

                                replyList.add(0, reply);
                                // Notify the RecyclerView adapter about the dataset change
                                replyAdapter.notifyItemInserted(0);

                                // Update comment number of current question.
                                currentQuestion.incrementCommentNumber();
                                questionRef.update("questions", currentQuestion.getCommentNumber());
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e(TAG, "Error getting user document", e);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding question", e);
                        // Handle the error
                    }
                });
    }
}
