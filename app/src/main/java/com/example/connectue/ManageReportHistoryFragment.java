package com.example.connectue;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
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
//    TODO: once other button is clicked, remember to clear/reinit reportIds
    private List<QueryDocumentSnapshot> reports;
//    TODO: ODO: once other button is clicked, remember to switch channel names
    private String currentChannel = General.POSTCOLLECTION;
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
    // TODO: Rename and change types and number of parameters
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
        return view;
    }

    private void initRecyclerReview(View view) {
        recyclerView = view.findViewById(R.id.admin_reported_RV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ReportedContentsRecyclerAdapter(reports, new ReportItemCallback() {
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
                    if (result != null) {
                        Log.i(TAG_Load_report, "" + result.size());
                        for (QueryDocumentSnapshot document: result) {
                            Log.i(TAG_Load_report, "" + document.getLong(General.REPORTCOUNTER));
                            reports.add(document);
                        }
                        Log.i(TAG_Load_report, "reportIds size: " + reports.size());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.i(TAG_Load_report, "null querySnapShot");
                    }
                } else {
                    Log.i(TAG_Load_report, "query failed.");
                }
            }
        });
    }

    private void loadContentToWorkBench(QueryDocumentSnapshot content) {
        String contentId = content.getString(General.REPORTCONTENTID);
        if (contentId == null || contentId.equals("")) {
            Log.i(TAG_load_content, "contentId is null ");
            return;
        }
        String count = content.getLong(General.REPORTCOUNTER) != null
                ? content.getLong(General.REPORTCOUNTER).toString() : "";
        DocumentReference documentReference = db.collection(currentChannel).document(contentId);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String imageURL = documentSnapshot.getString(General.POSTIMAGEURL) != null
                                ? documentSnapshot.getString(General.POSTIMAGEURL) : "";
                        String textContent = documentSnapshot.getString(General.POSTCONTENT) != null
                                ? documentSnapshot.getString(General.POSTCONTENT) : "";
                        Glide.with(getContext()).load(imageURL).into(contentIV);
                        contentTV.setText(textContent);
                        contentInfoTV.setText("Number of reports:" + count);
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
    }
}