package com.example.connectue.managers;

import com.example.connectue.model.Question;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
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

        tag = "QuestionManager class: ";
    }

    /**
     * Converts a FireBase document snapshot of a question collection into an instance
     * of comment model.
     * @param document FireBase document snapshot of the question collection.
     * @return Instance of the question model.
     */
    @Override
    protected Question deserialize(DocumentSnapshot document) {
        return new Question(
                document.getString("publisherName"),
                document.getId(),
                document.getString("publisher"),
                document.getString("text"),
                document.getLong("likes"),
                document.getLong("dislikes"),
                document.getLong("comments"),
                document.getTimestamp("timestamp").toDate());
    }

    /**
     * Converts an instance of the question model to a corresponding map for uploading to the
     * question collection.
     * @param question Instance of the question model.
     * @return Map for uploading to question collection.
     */
    @Override
    protected Map<String, Object> serialize(Question question) {
        Map<String, Object> questionData = new HashMap<>();

        questionData.put("publisher", question.getPublisherId());
        questionData.put("text", question.getText());
        questionData.put("likes", question.getLikeNumber());
        questionData.put("dislikes", question.getDislikeNumber());
        questionData.put("comments", question.getCommentNumber());
        questionData.put("timestamp", new Timestamp(question.getDatetime()));

        questionData.put("likedByUsers", new ArrayList<String>());

        return questionData;
    }
}
