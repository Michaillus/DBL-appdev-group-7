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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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
    private List<String> reportIds;
//    TODO: ODO: once other button is clicked, remember to switch channel names
    private String currentChannel = General.POSTCOLLECTION;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final String TAG_Load = "reportLoad";

    public ManageReportHistoryFragment() {
        // Required empty public constructor
        reportIds = new ArrayList<>();
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
        initRecyclerReview(view);

        loadReportedContents(currentChannel);
        return view;
    }

    private void initRecyclerReview(View view) {
        recyclerView = view.findViewById(R.id.admin_reported_RV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ReportedContentsRecyclerAdapter(reportIds, getContext());
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
                        Log.i(TAG_Load, "" + result.size());
                        for (QueryDocumentSnapshot document: result) {
                            Log.i(TAG_Load, "" + document.getLong(General.REPORTCOUNTER));
                            reportIds.add(document.getString(General.REPORTCONTENTID));
                        }
                        Log.i(TAG_Load, "reportIds size: " + reportIds.size());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.i(TAG_Load, "null querySnapShot");
                    }
                } else {
                    Log.i(TAG_Load, "query failed.");
                }
            }
        });
    }

//    private void set
}