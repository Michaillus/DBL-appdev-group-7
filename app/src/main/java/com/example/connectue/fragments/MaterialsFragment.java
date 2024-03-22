package com.example.connectue.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.R;
import com.example.connectue.activities.CourseViewActivity;
import com.example.connectue.adapters.MaterialAdapter;
import com.example.connectue.adapters.ReviewAdapter;
import com.example.connectue.firestoreManager.MaterialsManager;
import com.example.connectue.firestoreManager.ReviewManager;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.model.Material;
import com.example.connectue.model.Review;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MaterialsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MaterialsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private MaterialsManager materialsManager;
    private MaterialAdapter materialAdapter;

    private final int postsPerChunk = 4;
    private Boolean isLoading = false;
    private String TAG = "MaterialsFragUtil: ";
    private List<Material> materialList;
    private String courseId;


    public MaterialsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MaterialsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MaterialsFragment newInstance(String param1, String param2) {
        MaterialsFragment fragment = new MaterialsFragment();
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
        View view = inflater.inflate(R.layout.fragment_materials, container, false);

        RecyclerView materialsRecyclerView = view.findViewById(R.id.recyclerView_materials);

        materialsManager = new MaterialsManager(FirebaseFirestore.getInstance(),
                "materials",
                "material-likes",
                "material-dislikes");

        materialList = new ArrayList<>();
        materialAdapter = new MaterialAdapter(getContext(), materialList);
        materialsRecyclerView.setAdapter(materialAdapter);
        materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPosts();

        return view;
    }

    public void loadPosts() {
        materialsManager.downloadRecent(postsPerChunk, new FireStoreDownloadCallback<List<Material>>() {
            @Override
            public void onSuccess(List<Material> data) {
                //check course of opened page
                CourseViewActivity courseViewActivity = (CourseViewActivity) getActivity();
                if (courseViewActivity != null) {
                    courseId = courseViewActivity.getCourse();
                } else {
                    courseId = "";
                }
                //only add reviews linked to the opened course card
                for(Material material: data) {
                    if (material.getParentCourseId().equals(courseId)) {
                        materialList.add(material);
                    }
                }
                materialAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while downloading reviews", e);
            }
        });
    }
}