package com.example.connectue.managers;

import com.example.connectue.model.Material;
import com.example.connectue.model.Review;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MaterialsManager extends InteractableManager<Material>{
    public MaterialsManager(FirebaseFirestore db, String collectionName,
                         String likeCollectionName, String dislikeCollectionName, String commentCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName, commentCollectionName);
    }

    @Override
    protected Material deserialize(DocumentSnapshot document) {
        return new Material(
                document.getId(),
                document.getString("publisher"),
                document.getString("text"),
                document.getLong("likes"),
                document.getLong("dislikes"),
                document.getLong("comments"),
                document.getTimestamp("timestamp").toDate(),
                document.getString("parentCourseId"),
                document.getString("docURL")
        );
    }

    @Override
    protected Map<String, Object> serialize(Material material) {
        Map<String, Object> postData = new HashMap<>();


        postData.put("publisher", material.getPublisherId());
        postData.put("text", material.getText());
        postData.put("likes", material.getLikeNumber());
        postData.put("dislikes", material.getLikeNumber());
        postData.put("comments", material.getCommentNumber());
        postData.put("timestamp", new Timestamp(material.getDatetime()));
        postData.put("parentCourseId", material.getParentCourseId());
        postData.put("docURL", material.getDocUrl());

        return postData;
    }
}
