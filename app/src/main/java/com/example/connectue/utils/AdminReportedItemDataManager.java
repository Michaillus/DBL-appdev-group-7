package com.example.connectue.utils;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.connectue.fragments.ManageReportHistoryFragment;
import com.example.connectue.interfaces.ItemDeleteCallback;
import com.example.connectue.managers.ReviewManager;
import com.example.connectue.model.StudyUnit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;

/**
 * This class manages the data related to reported items to administrators.
 */
public class AdminReportedItemDataManager {
    String TAG_delete = "adminDelete";
    private final List<QueryDocumentSnapshot> reports;
    private final FirebaseFirestore db;
    private DocumentReference currentRequestReference = null;
    private DocumentReference currentContentReference = null;
    private DocumentSnapshot currentDocument = null;
    private String contentId = "";
    private final ManageReportHistoryFragment fragment;
    private String currentChannel;

    /**
     * Constructs a new AdminReportedItemDataManager.
     *
     * @param reports        List of reported documents
     * @param db             Firestore database instance
     * @param currentChannel The string representing the type of current reporting document
     * @param fragment       The fragment using this manager
     */
    public AdminReportedItemDataManager(List<QueryDocumentSnapshot> reports
            ,  FirebaseFirestore db, String currentChannel, ManageReportHistoryFragment fragment) {
        this.reports = reports;
        this.currentChannel = currentChannel;
        this.fragment = fragment;
        this.db = db;
    }

    /**
     * Set current channel to which the current reported item belongs.
     * @param currentChannel the string representing the collection to which the current reported item belongs.
     */
    public void setCurrentChannel(String currentChannel) {this.currentChannel = currentChannel;}

    /**
     * Return the channel of current reported document.
     * @return the string representing the collection to which the current reported item belongs.
     */
    public String getCurrentChannel(){return currentChannel;}

    /**
     * Loads reported contents from the database.
     */
    public void loadReportedContents() {
        // Query to retrieve reported documents from Firestore
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(General.REPORTEDCOLLECTION);
        Query targetQuery = collectionReference.whereEqualTo(General.REPORTFROMCOLLECTION, currentChannel)
                .orderBy(General.REPORTCOUNTER, Query.Direction.DESCENDING);
        targetQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    reports.clear();

                    if (result != null) {
                        // Add reported documents to the list
                        for (QueryDocumentSnapshot document: result) {
                            reports.add(document);
                        }
                    }
                    // Update data in the adapter
                    fragment.updateDataInAdapter();
                }
            }
        });
    }

    /**
     * Loads the reported content to the workbench.
     *
     * @param content The reported content
     */
    public void loadContentToWorkBench(QueryDocumentSnapshot content) {
        // Get the content ID from the QueryDocumentSnapshot
        contentId = content.getString(General.REPORTCONTENTID);
        // If the content ID is null or empty, return without further processing
        if (contentId == null || contentId.equals("")) {
            return;
        }

        String count = content.getLong(General.REPORTCOUNTER) != null
                ? content.getLong(General.REPORTCOUNTER).toString() : "";

        // Set the current references to the reported content and its corresponding request
        currentRequestReference = db.collection(General.REPORTEDCOLLECTION)
                .document(content.getId());
        currentContentReference = db.collection(currentChannel).document(contentId);

        // Retrieve the document snapshot of the reported content from the database
        currentContentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // If the retrieval is successful, get the document snapshot
                if (task.isSuccessful()) {
                    currentDocument = task.getResult();
                    // If the document exists, extract image URL and text content
                    if (currentDocument.exists()) {
                        String imageURL = currentDocument.getString(General.POSTIMAGEURL) != null
                                ? currentDocument.getString(General.POSTIMAGEURL) : "";
                        String textContent = currentDocument.getString(General.POSTCONTENT) != null
                                ? currentDocument.getString(General.POSTCONTENT) : "";
                        // Update workbench with content details
                        fragment.updateWorkbench(currentDocument, imageURL, textContent, count);
                    }
                }
            }
        });
    }

    /**
     * Deletes the reported content.
     * @param isImageFetchable Indicates if the content has an image to be fetched
     * @param imageURL         the string representing image document ID
     */
    public void deleteContent(boolean isImageFetchable, String imageURL) {
        // Check if the content has an associated image that needs to be deleted
        if (isImageFetchable) {
            // Get the StorageReference for the reported picture
            StorageReference reportedPicture = FirebaseStorage.getInstance().
                    getReferenceFromUrl(imageURL);
            // Delete the image from Firebase Storage
            reportedPicture.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // If image deletion is successful, proceed with deleting from the collection
                            Log.i(TAG_delete, "successful deleted picture ");
                            deleteFromCollection();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG_delete, "failed deleted picture ");
                        }
                    });
        } else {
            deleteFromCollection();
        }
    }

    /**
     * Deletes the reported content from the collection in firebase.
     */
    private void deleteFromCollection() {
        if (currentChannel.equals(StudyUnit.COURSE_REVIEW_COLLECTION_NAME) ||
                currentChannel.equals(StudyUnit.MAJOR_REVIEW_COLLECTION_NAME)) {
            // Deleting course or major review through the study unit manager
            StudyUnit.StudyUnitType studyUnitType;
            // Finds the type of study unit of the content
            if (currentChannel.equals(StudyUnit.COURSE_REVIEW_COLLECTION_NAME)) {
                studyUnitType = StudyUnit.StudyUnitType.COURSE;
            } else {
                studyUnitType = StudyUnit.StudyUnitType.MAJOR;
            }
            // Initialize review manager for deletion
            ReviewManager reviewManager = new ReviewManager(FirebaseFirestore.getInstance(),
                    StudyUnit.getReviewCollectionName(studyUnitType),
                    StudyUnit.getReviewLikeCollectionName(studyUnitType),
                    StudyUnit.getReviewDislikeCollectionName(studyUnitType),
                    StudyUnit.getReviewCommentCollectionName(studyUnitType));
            // Delete the review
            reviewManager.deleteReview(contentId, studyUnitType,
                    new ItemDeleteCallback() {
                        @Override
                        public void onSuccess() {
                            // Remove the report if review is deleted successfully
                            removeRequest();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.i(TAG_delete, "failed to remove course reviews");
                        }
                    });
        } else {
            // Deleting post or comment directly
            currentContentReference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            removeRequest();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG_delete, "failed deleted from the content collection.");
                        }
                    });
        }
    }

    /**
     * Removes the report request from Firestore after the reported item is handled.
     * After successful deletion of the request, it triggers a reset to clean up the state.
     */
    public void removeRequest() {
        currentRequestReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Reset the state after successful deletion of the request
                        resetAfterDeleteKeep();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log a message if deletion of the request fails
                        Log.i(TAG_delete, "failed deleted the request.");
                    }
                });
    }

    /**
     * Resets the state of the manager after a successful deletion of a reported item.
     * It clears the current document, request reference, content reference, and reports list.
     * Then, it reloads the reported contents and triggers a reset in the associated fragment's data.
     */
    public void resetAfterDeleteKeep() {
        // Clearing current state variables
        currentDocument = null;
        currentRequestReference = null;
        currentContentReference = null;
        reports.clear();
        // Reload reported contents and trigger a reset in the fragment's data
        loadReportedContents();
        fragment.resetData();
    }

    /**
     * To get the currentDocument
     * @return currentDocument
     */
    public DocumentSnapshot getCurrentDoc() {
        return currentDocument;
    }

    public DocumentReference getCurrentContentReference() {
        return currentContentReference;
    }

    public DocumentReference getCurrentRequestReference() {
        return currentRequestReference;
    }
}
