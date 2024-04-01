package com.example.connectue.managers;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.model.Question;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionManager extends InteractableManager<Question> {

    /**
     * Constructor for a manager of questions.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of the collection that stores questions in the database.
     * @param likeCollectionName Name of the collection that stores question likes in the database.
     * @param dislikeCollectionName Name of the collection that stores question dislikes in the database.
     * @param commentCollectionName Name of the collection that stores question comments in the database.
     */
    public QuestionManager(FirebaseFirestore db, String collectionName,
                       String likeCollectionName, String dislikeCollectionName,
                       String commentCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName, commentCollectionName);

        tag = "QuestionManager";
    }

    /**
     * Retrieves {@code amount} of latest questions on the {@code courseId} course
     * starting from {@code lastRetrieved} and passes list of their models through
     * {@code onSuccess} method of {@code callback}.
     * If download of questions has failed, passes the error message through {@code onFailure}
     * method of the {@code callback}.
     * @param courseId id of the course which questions to retrieve.
     * @param amount number of reviews to retrieve.
     * @param callback Callback to pass list of retrieved questions or an error message.
     */
    public void downloadRecent(String courseId, int amount, ItemDownloadCallback<List<Question>> callback) {
        Query basicQuery = collection.whereEqualTo("parentCourseId", courseId);
        super.downloadRecentWithQuery(basicQuery, amount, callback);
    }

    /**
     * Converts a FireBase document snapshot of a question collection into an instance
     * of comment model.
     * @param document FireBase document snapshot of the question collection.
     * @return Instance of the question model.
     */
    @Override
    public Question deserialize(DocumentSnapshot document) {
        return new Question(
                document.getId(),
                document.getString("publisher"),
                document.getString("text"),
                document.getLong("likes"),
                document.getLong("dislikes"),
                document.getLong("comments"),
                document.getTimestamp("timestamp").toDate(),
                document.getString("parentCourseId"));
    }

    /**
     * Converts an instance of the question model to a corresponding map for uploading to the
     * question collection.
     * @param question Instance of the question model.
     * @return Map for uploading to question collection.
     */
    @Override
    public Map<String, Object> serialize(Question question) {
        Map<String, Object> questionData = new HashMap<>();

        questionData.put("publisher", question.getPublisherId());
        questionData.put("text", question.getText());
        questionData.put("likes", question.getLikeNumber());
        questionData.put("dislikes", question.getDislikeNumber());
        questionData.put("comments", question.getCommentNumber());
        questionData.put("timestamp", new Timestamp(question.getDatetime()));
        questionData.put("parentCourseId", question.getParentCourseId());

        questionData.put("likedByUsers", new ArrayList<String>());

        return questionData;
    }
}
