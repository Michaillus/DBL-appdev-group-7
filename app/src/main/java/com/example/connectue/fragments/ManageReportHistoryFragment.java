package com.example.connectue.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.connectue.interfaces.ItemDeleteCallback;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.managers.ReviewManager;
import com.example.connectue.managers.StudyUnitManager;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.utils.General;
import com.example.connectue.R;
import com.example.connectue.interfaces.ReportItemCallback;
import com.example.connectue.adapters.ReportedContentsAdapter;
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

import java.util.ArrayList;
import java.util.List;

/**
 * The workbench for administrator to see the reported content, and choose to delete or keep
 *  the contents.
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageReportHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageReportHistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    private List<QueryDocumentSnapshot> reports;
    private String currentChannel = General.POSTCOLLECTION;
    private DocumentSnapshot currentDocument = null;
    private DocumentReference currentContentReference = null;
    private DocumentReference currentRequestReference = null;
    private boolean isImageFetchable = false;
    private String imageURL = "";
    private static final String DEFAULT_CONTENT = "Content Here";
    private static final String DEFAULT_COUNT = "Number of reports: ";
    private Button postChannelBtn;
    private Button commentChannelBtn;
    private Button courseReviewChannelBtn;
    private Button deleteBtn;
    private Button keepBtn;
    private ImageView contentIV;
    private TextView contentTV;
    private TextView contentInfoTV;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final String TAG_load_content = "loadContent";
    private static final String TAG_Load_report = "reportLoad";
    private static final String TAG_delete = "delete";
     //Id of the content to delete.
    String contentId;

    public ManageReportHistoryFragment() {
        // Required empty public constructor
        reports = new ArrayList<>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageReportHistoryFragment.
     */
    public static ManageReportHistoryFragment newInstance(String param1, String param2) {
        ManageReportHistoryFragment fragment = new ManageReportHistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_manage_report_history, container, false);
        initComponents(view);
        initRecyclerReview(view);

        loadReportedContents(currentChannel);

        initPostButton();
        initCommentButton();
        initCourseReviewButton();

        return view;
    }

    private void initRecyclerReview(View view) {
        recyclerView = view.findViewById(R.id.admin_reported_RV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ReportedContentsAdapter(reports, new ReportItemCallback() {
            @Override
            public void itemClicked(QueryDocumentSnapshot content) {
                loadContentToWorkBench(content);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    private void loadReportedContents(String fromCollection) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(General.REPORTEDCOLLECTION);
        Query targetQuery = collectionReference.whereEqualTo(General.REPORTFROMCOLLECTION, fromCollection)
                .orderBy(General.REPORTCOUNTER, Query.Direction.DESCENDING);
        targetQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    reports.clear();

                    if (result != null) {
//                        reports.clear();
                        Log.i(TAG_Load_report, "" + result.size());
                        for (QueryDocumentSnapshot document: result) {
                            Log.i(TAG_Load_report, "" + document.getLong(General.REPORTCOUNTER));
                            reports.add(document);
                        }
                        Log.i(TAG_Load_report, "reportIds size: " + reports.size());
//                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.i(TAG_Load_report, "null querySnapShot");
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.i(TAG_Load_report, "query failed.");
                }
            }
        });
    }

    private void loadContentToWorkBench(QueryDocumentSnapshot content) {
        contentId = content.getString(General.REPORTCONTENTID);
        if (contentId == null || contentId.equals("")) {
            Log.i(TAG_load_content, "contentId is null ");
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
                        imageURL = currentDocument.getString(General.POSTIMAGEURL) != null
                                ? currentDocument.getString(General.POSTIMAGEURL) : "";
                        String textContent = currentDocument.getString(General.POSTCONTENT) != null
                                ? currentDocument.getString(General.POSTCONTENT) : "";
                        setContentIV(imageURL);
                        contentTV.setText(textContent);
                        contentInfoTV.setText(DEFAULT_COUNT + count);
                        setDeleteKeepButton();
                    } else {
                        Log.i(TAG_load_content
                                , "the reported content dose not exist in the related collection");
                    }
                } else {
                    Log.i(TAG_load_content, "fetch reported content failed");
                }
            }
        });
    }

    /**
     * I
     * @param view
     */
    private void initComponents(View view) {
        db = FirebaseFirestore.getInstance();
        postChannelBtn = view.findViewById(R.id.report_load_post_btn);
        commentChannelBtn = view.findViewById(R.id.report_load_comment_btn);
        courseReviewChannelBtn = view.findViewById(R.id.report_load_course_review_btn);
        deleteBtn = view.findViewById(R.id.report_delete_btn);
        keepBtn = view.findViewById(R.id.report_keep_btn);
        contentIV = view.findViewById(R.id.report_picture_IV);
        contentTV = view.findViewById(R.id.report_text_content_tv);
        contentInfoTV = view.findViewById(R.id.report_basic_info_tv);

        contentTV.setText(DEFAULT_CONTENT);
        contentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        contentInfoTV.setText(DEFAULT_COUNT);

        setDefaultContentIV();
    }

    private void setDefaultContentIV() {
        contentIV.setImageResource(R.drawable.baseline_person_24);
        contentIV.setOnClickListener(null);
    }

    private void setContentIV(String url) {
        Glide.with(getContext()).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                setDefaultContentIV();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                isImageFetchable = true;
                return false;
            }
        }).into(contentIV);
    }

    private void setDeleteKeepButton() {
        if (currentDocument == null || !currentDocument.exists()) {
            disableDeleteKeepButton();
            return;
        }
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContent();
            }
        });
        keepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keepContent();
            }
        });
    }

    private void disableDeleteKeepButton() {
        deleteBtn.setOnClickListener(null);
        keepBtn.setOnClickListener(null);
    }

    private void deleteContent() {
        Log.i(TAG_delete, "before delete picture, picture fetchable: " + isImageFetchable);
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
                            Toast.makeText(getContext()
                                    , "Remove picture failed, check log"
                                    , Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(),
                            "Remove content from it collection failed, check log",
                            Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(),
                                    "Remove content from it collection failed, check log",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void keepContent() {
        removeRequest();
    }

    private void removeRequest() {
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
                        Toast.makeText(getContext(), "Remove request failed, check log",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetAfterDeleteKeep() {
        currentDocument = null;
        currentRequestReference = null;
        currentContentReference = null;
        isImageFetchable = false;
        imageURL = "";
        setDefaultContentIV();
        contentTV.setText(DEFAULT_CONTENT);
        contentInfoTV.setText(DEFAULT_COUNT);
        disableDeleteKeepButton();
        reports.clear();
        loadReportedContents(currentChannel);
    }

    private void initPostButton() {
        postChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentChannel = General.POSTCOLLECTION;
                resetAfterDeleteKeep();
                loadReportedContents(currentChannel);
            }
        });

    }
    private void initCommentButton() {
        commentChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentChannel = General.COMMENTCOLLECTION;
                resetAfterDeleteKeep();
                loadReportedContents(currentChannel);
            }
        });
    }
    private void initCourseReviewButton() {
        courseReviewChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentChannel = General.COURSEREVIEWCOLLECTION;
                resetAfterDeleteKeep();
                loadReportedContents(currentChannel);
            }
        });
    }
}