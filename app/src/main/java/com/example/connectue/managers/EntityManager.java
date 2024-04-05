package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.interfaces.ItemDeleteCallback;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Database request manager for the documents of model class {@code T}. Responsible for converting
 * database document snapshots to object of class {@code T} and vice versa, provides methods for
 * database upload and download requests of the model objects.
 * @param <T> Model class corresponding to the documents of the target collection.
 */
public abstract class EntityManager<T> {

    /**
     * Class tag for logs.
     */
    protected String tag = "EntityManager class: ";

    /**
     * Reference to the firebase collection, that is assigned to the manager.
     */
    CollectionReference collection;

    /**
     * Last document that was downloaded from database, starting from the most recent, in
     * descending order.
     */
    protected DocumentSnapshot lastRetrieved;

    /**
     * Constructor for entity manager given instance of  FireStore database and the name of
     * collection in that database.
     * @param db Instance of a FireStore database.
     * @param collectionName Name of collection in the database {@code db}.
     */
    protected EntityManager(FirebaseFirestore db, String collectionName) {
        collection = db.collection(collectionName);
        lastRetrieved = null;
    }

    /**
     * Asynchronously downloads a document from the {@code collection} of the database by
     * it's id and passes model object through {@code onSuccess} method of {@code callback}.
     * If download of the document has failed, passes the error message through {@code onFailure}
     * method of the {@code callback}.
     * @param documentId Id of the document to retrieve from {@code collection}.
     * @param callback Callback to pass model object of the retrieved document or an error message.
     */
    public void downloadOne(String documentId, ItemDownloadCallback<T> callback) {
        collection.document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.i(tag, "Document is downloaded successfully");
                        callback.onSuccess(deserialize(documentSnapshot));
                    } else {
                        Exception e = new NoSuchElementException("Document does not exit");
                        Log.e(tag, "Document does not exits", e);
                        callback.onFailure(e);
                    }
                }).addOnFailureListener(e -> {
                    Log.e(tag, "Error getting the document", e);
                    callback.onFailure(e);
                });
    }

    /**
     * Asynchronously downloads {@code amount} of latest documents starting from {@code lastRetrieved}
     * and passes list of their models through {@code onSuccess} method of {@code callback}.
     * If download of the documents has failed, passes the error message through {@code onFailure}
     * method of the {@code callback}.
     * @param amount number of documents to download
     * @param callback Callback to pass list of models of the retrieved documents or
     *                 an error message.
     */
    public void downloadRecent(int amount, ItemDownloadCallback<List<T>> callback) {
        downloadRecentWithQuery(collection, amount, callback);
    }

    protected void downloadRecentWithQuery(Query basicQuery, int amount,
                                         ItemDownloadCallback<List<T>> callback) {
        Query query = basicQuery.orderBy("timestamp", Query.Direction.DESCENDING);
        if (lastRetrieved != null) {
            query = query.startAfter(lastRetrieved);
        }
        query = query.limit(amount);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (!snapshot.isEmpty()) {
                    lastRetrieved = snapshot.getDocuments().get(task.getResult().size() - 1);
                    List<T> data = deserializeList(task.getResult());
                    callback.onSuccess(data);
                } else {
                    Log.i(tag, "No documents found");
                }
            } else {
                Log.e(tag, "Error getting documents", task.getException());
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Asynchronously downloads all the documents in the {@code collection}
     * and passes list of their models through {@code onSuccess} method of {@code callback}.
     * If download of the documents has failed, passes the error message through {@code onFailure}
     * method of the {@code callback}.
     * @param callback Callback to pass list of models of the retrieved documents or
     *                 an error message.
     */
    public void downloadAll(ItemDownloadCallback<List<T>> callback) {
        downloadAllWithQuery(collection, callback);
    }

    protected void downloadAllWithQuery(Query basicQuery, ItemDownloadCallback<List<T>> callback) {

        basicQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<T> data = deserializeList(task.getResult());
                callback.onSuccess(data);
            } else {
                Log.e(tag, "Error getting documents", task.getException());
            }
        });

    }

    /**
     * Converts list of FireBase document snapshot to a list of corresponding objects
     * of model {@code T}.
     * @param snapshot list of FireBase document snapshots.
     * @return List of objects of model {@code T} .
     */
    protected List<T> deserializeList(QuerySnapshot snapshot) {
        List<T> data = new ArrayList<T>();
        for (QueryDocumentSnapshot document : snapshot) {
            data.add(deserialize(document));
        }
        return data;
    }

    /**
     * Convert an object of class {@code T} to the document of {@code collection}
     * and uploads in to the database. Calls {@code callback.onSuccess} when document is uploaded.
     * If upload of the documents has failed, passes the error message
     * through {@code callback.onFailure}
     * @param object Object to upload to the database.
     * @param callback Callback that is called when object is uploaded or the upload has failed.
     */
    public void upload(T object, ItemUploadCallback callback) {
        Map<String, Object> data = serialize(object);
        collection.add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.i(tag, "Document is successfully uploaded to FireStore");
                    callback.onSuccess();
                }).addOnFailureListener(e -> {
                    Log.e(tag, "Failed to upload to FireStore", e);
                    callback.onFailure(e);
                });
    }

    /**
     * Set (uploads) an instance of the model to the specifies id in the collection in database.
     * @param object instance of the model to set on id
     * @param objectId Id of the object in the database
     * @param callback Callback that is called when the document is set.
     */
    public void set(T object, String objectId, ItemUploadCallback callback) {
        Map<String, Object> data = serialize(object);
        collection.document(objectId).set(data)
                .addOnSuccessListener(documentReference -> {
                    Log.i(tag, "Document is successfully set to FireStore");
                    callback.onSuccess();
                }).addOnFailureListener(e -> {
                    Log.e(tag, "Failed to set document to FireStore", e);
                    callback.onFailure(e);
                });
    }

    protected void update(String documentId, String field, Object value,
                          ItemUploadCallback callback) {
        collection.document(documentId).update(field, value)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    protected void delete(String documentId, ItemDeleteCallback callback) {
        collection.document(documentId).delete()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    protected void resetLastRetrieved() {
        lastRetrieved = null;
    }

    /**
     * Converts a FireBase document snapshot into the corresponding instance of model {@code T}.
     * @param document FireBase document snapshot to be converted.
     * @return Instance of the model corresponding to the document.
     */
    public abstract T deserialize(DocumentSnapshot document);

    /**
     * Converts an instance of the model {@code T} to the corresponding map for uploading to the
     * {@code collection}.
     * @param object Instance of the model.
     * @return Map for uploading to {@code collection}.
     */
    public abstract Map<String, Object> serialize(T object);
}
