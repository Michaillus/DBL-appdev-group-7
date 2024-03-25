package com.example.connectue.fragmets;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.R;
import com.example.connectue.adapters.QuestionAdapter;
import com.example.connectue.adapters.ReviewAdapter;
import com.example.connectue.managers.QuestionManager;
import com.example.connectue.managers.ReviewManager;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.interfaces.FireStoreUploadCallback;
import com.example.connectue.model.Question;
import com.example.connectue.model.Review;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String TAG = "QuestionsFragment class: ";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // List of posts to output in feed.
    private List<Question> questionList;

    // Adapter that is responsible for outputting posts to UI.
    private QuestionAdapter questionAdapter;

    // Number of posts loaded at once to the feed.
    private final int postsPerChunk = 1;

    // Manager for database requests for posts collection.
    private QuestionManager questionManager;

    // Indicates if posts are currently loading from database
    private Boolean isLoading = false;
    private String questionerId;
    private FirebaseFirestore db;
    DocumentReference questionRef;
    private String questionId;

    public QuestionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuestionsFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questions, container, false);

        RecyclerView questionRecyclerView = view.findViewById(R.id.recyclerView_question);

        // Initialize database post manager.
        questionManager = new QuestionManager(FirebaseFirestore.getInstance(), "questions", "questions-likes", "questions-dislikes", "comments");

        // Initializing list of reviews
        questionList = new ArrayList<>();

        questionAdapter = new QuestionAdapter(getContext(), questionList);
        questionRecyclerView.setAdapter(questionAdapter);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();


        // Upload from database and display first chunk of posts
        loadPosts();

        // Add a scroll listener to the RecyclerView
        questionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) questionRecyclerView.getLayoutManager();
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
                    loadPosts();
                }
            }
        });



        // Inflate the layout for this fragment
        return view;
    }


    // Generating dummy questions for testing.
    // TODO: write question retrieval from database

    public void loadPosts() {

        questionManager.downloadRecent(postsPerChunk, new FireStoreDownloadCallback<List<Question>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Question> data) {
                questionList.addAll(data);
                questionAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while downloading questions", e);
            }
        });
    }


}