package com.example.connectue.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.connectue.managers.QuestionManager;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.model.Question;
import com.example.connectue.utils.ActivityUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connectue.R;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddQuestionActivity extends AppCompatActivity {

    /**
     * Class tag for logs.
     */
    private static final String tag = "AddQuestionActivity";

    QuestionManager questionManager;
    EditText questionDescription;
    Button publishQuestionBtn;
    FloatingActionButton backBtn;
    String text;

    /**
     * Course for which question is added.
     */
    private StudyUnit course;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        // Initializing question manager
        questionManager = new QuestionManager(FirebaseFirestore.getInstance(),
                Question.QUESTION_COLLECTION_NAME,
                Question.QUESTION_LIKE_COLLECTION_NAME,
                Question.QUESTION_DISLIKE_COLLECTION_NAME,
                Question.QUESTION_COMMENT_COLLECTION_NAME);

        questionDescription = findViewById(R.id.reviewDescription);
        publishQuestionBtn = findViewById(R.id.publishReviewBtn);
        backBtn = findViewById(R.id.back_Btn);

        // Retrieve course model passed from the previous activity / fragment.
        course = ActivityUtils.getCourse(this, savedInstanceState);

        publishQuestionBtn.setOnClickListener(v -> publishQuestion());
        backBtn.setOnClickListener(v -> finish());

    }

    private void publishQuestion() {
        publishQuestionBtn.setEnabled(false);
        questionDescription.setEnabled(false);
        text = questionDescription.getText().toString().trim();
        uploadToFirestore(text);
    }

    private void uploadToFirestore(String text) {
        String publisherId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Question question = new Question(publisherId, text, course.getId());

        questionManager.upload(question, new ItemUploadCallback() {
            @Override
            public void onSuccess() {
                Log.i(tag, "Question is uploaded successfully");
                Toast.makeText(AddQuestionActivity.this,
                        "Question is published successfully", Toast.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Failed to upload the question: " + e.getMessage());
                Toast.makeText(AddQuestionActivity.this,
                        "Failed to publish your question", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

