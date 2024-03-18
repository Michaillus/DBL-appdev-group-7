package com.example.connectue.firestoreManager;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EntityManager<T> {

    private static final String TAG = "Entity Manager class: ";

    CollectionReference collection;

    protected DocumentSnapshot lastRetrieved;

    public EntityManager(FirebaseFirestore db, String collectionName) {
        collection = db.collection(collectionName);
        lastRetrieved = null;
    }

    public void downloadOne(String documentId, FireStoreDownloadCallback<T> callback) {
        collection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.i(TAG, "Document is downloaded successfully");
                callback.onSuccess(deserialize(documentSnapshot));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error getting the document", e);
                    callback.onFailure(e);
                }
        });
    }

    public void downloadRecent(int amount, FireStoreDownloadCallback<List<T>> callback) {
        Query basicQuery = collection.orderBy("timestamp", Query.Direction.DESCENDING);
        Query currentQuery;
        if (lastRetrieved == null) {
            currentQuery = basicQuery.limit(amount);
        } else {
            currentQuery = basicQuery.startAfter(lastRetrieved).limit(amount);
        }
        currentQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (!snapshot.isEmpty()) {
                    lastRetrieved = snapshot.getDocuments().get(task.getResult().size() - 1);
                    List<T> data = deserializeList(task.getResult());
                    callback.onSuccess(data);
                }
            } else {
                Log.e(TAG, "Error getting documents", task.getException());
            }
        });
    }

    protected List<T> deserializeList(QuerySnapshot snapshot) {
        List<T> data = new ArrayList<T>();
        for (QueryDocumentSnapshot document : snapshot) {
            data.add(deserialize(document));
        }
        return data;
    }

    public void upload(T object, FireStoreUploadCallback callback) {
        Map<String, Object> data = serialize(object);
        collection.add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i(TAG, "Document is successfully uploaded to FireStore");
                        callback.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to upload to FireStore", e);
                        callback.onFailure(e);
                    }
                });
    }

    protected void update(String documentId, String field, Object value,
                          FireStoreUploadCallback callback) {
        collection.document(documentId).update(field, value)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    public void resetLastRetrieved() {
        lastRetrieved = null;
    }

    protected abstract T deserialize(DocumentSnapshot document);

    protected abstract Map<String, Object> serialize(T object);
}
