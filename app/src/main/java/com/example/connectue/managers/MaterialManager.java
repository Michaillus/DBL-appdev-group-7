package com.example.connectue.managers;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.model.Material;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialManager extends InteractableManager<Material>{
    public MaterialManager(FirebaseFirestore db, String collectionName,
                           String likeCollectionName, String dislikeCollectionName, String commentCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName, commentCollectionName);
    }

    /**
     * Retrieves {@code amount} of latest study materials on the {@code courseId} course
     * starting from {@code lastRetrieved} and passes list of their models through
     * {@code onSuccess} method of {@code callback}.
     * If download of reviews has failed, passes the error message through {@code onFailure}
     * method of the {@code callback}.
     * @param courseId id of the course which study materials to retrieve.
     * @param amount number of study materials to retrieve.
     * @param callback Callback to pass list of retrieved study materials or an error message.
     */
    public void downloadRecent(String courseId, int amount, ItemDownloadCallback<List<Material>> callback) {
        Query basicQuery = collection.whereEqualTo("parentCourseId", courseId);
        super.downloadRecentWithQuery(basicQuery, amount, callback);
    }

    /**
     * Converts a FireBase document snapshot of a material collection into an instance
     * of material model.
     * @param document FireBase document snapshot of the material collection.
     * @return Instance of the material model.
     */
    @Override
    public Material deserialize(DocumentSnapshot document) {
        return new Material(
                document.getId(),
                document.getString("publisher"),
                document.getString("text"),
                document.getLong("likes"),
                document.getLong("dislikes"),
                document.getLong("comments"),
                document.getTimestamp("timestamp").toDate() != null ? document.getTimestamp("timestamp").toDate() : null,
                document.getString("parentCourseId"),
                document.getString("docURL")
        );
    }

    /**
     * Converts an instance of the material model to a corresponding map for uploading to the
     * material collection.
     * @param material Instance of the material model.
     * @return Map for uploading to material collection.
     */
    @Override
    public Map<String, Object> serialize(Material material) {
        Map<String, Object> postData = new HashMap<>();


        postData.put("publisher", material.getPublisherId());
        postData.put("text", material.getText());
        postData.put("likes", material.getLikeNumber());
        postData.put("dislikes", material.getDislikeNumber());
        postData.put("comments", material.getCommentNumber());
        postData.put("timestamp", new Timestamp(material.getDatetime()));
        postData.put("parentCourseId", material.getParentCourseId());
        postData.put("docURL", material.getDocUrl());

        return postData;
    }
}
