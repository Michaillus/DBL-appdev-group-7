package com.example.connectue.fragments;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.R;
import com.example.connectue.activities.AddMaterialActivity;
import com.example.connectue.activities.CourseViewActivity;
import com.example.connectue.activities.StudyUnitViewActivity;
import com.example.connectue.adapters.MaterialAdapter;
import com.example.connectue.databinding.FragmentMaterialsBinding;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.MaterialManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.model.Material;
import com.example.connectue.model.User;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for the home page with the post feed and add a post button.
 */
public class MaterialsFragment extends Fragment {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "Fragment";

    private FragmentMaterialsBinding binding;

    /**
     * Adapter that is responsible for outputting posts to UI.
     */
    private MaterialAdapter materialAdapter;

    /**
     * Manager for database requests for posts collection.
     */
    private MaterialManager materialManager;

    /**
     * Indicates if posts are currently loading from database.
     */
    private Boolean isLoading = false;

    /**
     * Indicates if posts are currently loading from database.
     */
    private FragmentManager fragmentManager;

    /**
     * Course model of the opened page.
     */
    StudyUnit course;

    /**
     * Activity result launcher for launching the reload method on finish add material activity.
     */
    ActivityResultLauncher<Intent> activityResultLauncher;

    public MaterialsFragment() {
        // Default constructor
    }

    public MaterialsFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMaterialsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Defines listener for reloading the study unit view activity when user returns from
        // add material activity page.
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.e(TAG, "Does this work?");
                    StudyUnitViewActivity studyUnitViewActivity = (StudyUnitViewActivity) getActivity();
                    if (studyUnitViewActivity != null) {
                        studyUnitViewActivity.reload();
                    } else {
                        Log.e(TAG, "Unable to get Study unit view activity");
                    }

                });

        // Define reviews recycler view.
        RecyclerView materialsRecyclerView = binding.recyclerViewMaterials;

        // Initialize database post manager.
        materialManager = new MaterialManager(FirebaseFirestore.getInstance(),
                Material.MATERIAL_COLLECTION_NAME,
                Material.MATERIAL_LIKE_COLLECTION_NAME,
                Material.MATERIAL_DISLIKE_COLLECTION_NAME,
                Material.MATERIAL_COMMENT_COLLECTION_NAME);

        // Initialize the course.
        course = retrieveCourse();

        // Initializing the list of posts in the feed.
        List<Material> materialList = new ArrayList<>();

        //Initialize RecyclerView
        initRecyclerView(materialList, materialsRecyclerView);

        // Upload from database and display first chunk of posts
        loadMaterials(materialList);

        // Display the createPostBtn only for verified users
        displayAddMaterialButton(root);

        // Add a scroll listener to the RecyclerView
        getPostsOnScroll(materialList, materialsRecyclerView);

        return root;
    }

    private void initRecyclerView(List<Material> materialList, RecyclerView materialRecyclerView) {
        materialAdapter = new MaterialAdapter(materialList, fragmentManager);
        materialRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        materialRecyclerView.setHasFixedSize(false);
        materialRecyclerView.setAdapter(materialAdapter);
    }

    private void loadMaterials(List<Material> materialList) {
        int materialsPerChunk = 8;
        materialManager.downloadRecent(course.getId(), materialsPerChunk, new ItemDownloadCallback<List<Material>>() {
            @Override
            public void onSuccess(List<Material> data) {
                materialList.addAll(data);
                materialAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while downloading materials", e);
            }
        });
    }

    private void displayAddMaterialButton(View root) {
        UserManager userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ExtendedFloatingActionButton addQuestionBtn = root.findViewById(R.id.addQuestionBtn);
        userManager.downloadOne(currentUid, new ItemDownloadCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.getRole() == User.STUDENT_USER_ROLE || user.getRole() == User.ADMIT_USER_ROLE) {
                    Log.i(TAG, "User is allowed to add a material");
                    addQuestionBtn.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), AddMaterialActivity.class);
                        intent.putExtra("course", course.studyUnitToString());

                        activityResultLauncher.launch(intent);
                    });
                    addQuestionBtn.setVisibility(View.VISIBLE);
                } else {
                    Log.i(TAG, "User is not allowed to add a material");
                    addQuestionBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while retrieving user from the database", e);
            }
        });
    }

    private void getPostsOnScroll(List<Material> materialList, RecyclerView materialRecyclerView) {
        materialRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Check if end of the list is reached
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    // Assuming PAGE_SIZE is the number of items to load per page
                    // Load more items
                    isLoading = true;
                    loadMaterials(materialList);
                }
            }
        });
    }

    /**
     * Retrieves the course model of current page.
     */
    private StudyUnit retrieveCourse() {
        CourseViewActivity courseViewActivity = (CourseViewActivity) getActivity();
        if (courseViewActivity != null) {
            return courseViewActivity.getStudyUnit();
        } else {
            return new StudyUnit("0", "0", StudyUnit.StudyUnitType.COURSE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}