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

    public AdminReportedItemDataManager(List<QueryDocumentSnapshot> reports
            ,  FirebaseFirestore db, String currentChannel, ManageReportHistoryFragment fragment) {
        this.reports = reports;
        this.currentChannel = currentChannel;
        this.fragment = fragment;
        this.db = db;
    }

    public void setCurrentChannel(String currentChannel) {this.currentChannel = currentChannel;}
    public String getCurrentChannel(){return currentChannel;}

    public void loadReportedContents() {
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
                        for (QueryDocumentSnapshot document: result) {
                            reports.add(document);
                        }
                    }
                    fragment.updateDataInAdapter();
                }
            }
        });
    }

    public void loadContentToWorkBench(QueryDocumentSnapshot content) {
        contentId = content.getString(General.REPORTCONTENTID);
        if (contentId == null || contentId.equals("")) {
            return;
        }

        String count = content.getLong(General.REPORTCOUNTER) != null
                ? content.getLong(General.REPORTCOUNTER).toString() : "";
        currentRequestReference = db.collection(General.REPORTEDCOLLECTION)
                .document(content.getId());
        currentContentReference = db.collection(currentChannel).document(contentId);
        currentContentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    currentDocument = task.getResult();
                    if (currentDocument.exists()) {
                        String imageURL = currentDocument.getString(General.POSTIMAGEURL) != null
                                ? currentDocument.getString(General.POSTIMAGEURL) : "";
                        String textContent = currentDocument.getString(General.POSTCONTENT) != null
                                ? currentDocument.getString(General.POSTCONTENT) : "";
                        fragment.updateWorkbench(currentDocument, imageURL, textContent, count);
                    }
                }
            }
        });
    }

    public void deleteContent(boolean isImageFetchable, String imageURL) {
        if (isImageFetchable) {
            StorageReference reportedPicture = FirebaseStorage.getInstance().
                    getReferenceFromUrl(imageURL);
            reportedPicture.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
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

    public void removeRequest() {
        currentRequestReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        resetAfterDeleteKeep();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG_delete, "failed deleted the request.");
                    }
                });
    }

    public void resetAfterDeleteKeep() {
        currentDocument = null;
        currentRequestReference = null;
        currentContentReference = null;
        reports.clear();
        loadReportedContents();
        fragment.resetData();
    }
}
