package com.example.connectue.fragments;

import static com.example.connectue.utils.General.PROFILEPICTURE;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.connectue.model.Interactable;
import com.example.connectue.utils.General;
import com.example.connectue.R;
import com.example.connectue.activities.AdminActivity;
import com.example.connectue.activities.LoadingActivity;
import com.example.connectue.activities.PostHistoryActivity;
import com.example.connectue.utils.ProfilePageBasicInfo;
import com.example.connectue.utils.ProfilePageHistoryFunction;
import com.example.connectue.utils.ProfilePagePictureOperation;
import com.example.connectue.utils.ProfilePageSignoutDelete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = "Profile";
    private static final String TAG_Profile = "ProfilePic";
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentSnapshot document;
    private long role = 2;
    Button adminBtn;
    private ProfilePageBasicInfo basicInfo;
    private ProfilePageSignoutDelete signoutDeleteModule;
    private ProfilePageHistoryFunction profilePageHistoryFunction;
    private ProfilePagePictureOperation profilePagePictureOperation;
    public ProfileFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initData(view);
        return view;
    }

    /**
     * Fetch the user data from the Firebase Firestore, and then based on the data to initialize
     *  all components on this page.
     * @param view to fetch components on this page.
     */
    private void initData(View view) {
        Log.i(TAG, "start executing onCreate");
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference = db.collection(General.USERCOLLECTION).document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();

                    if (document.exists()) {
                        Log.i(TAG,"document fetched");
                        Log.i(TAG, document.toString());
                        parseDocument();
                        if (getContext() != null) {
                            basicInfo = new ProfilePageBasicInfo(getContext(), view, document, getResources(), db, user);
                            signoutDeleteModule = new ProfilePageSignoutDelete(getContext(), getActivity(), view, db, user);
                            profilePageHistoryFunction = new ProfilePageHistoryFunction(getContext(), getActivity(), view, document);
                            profilePagePictureOperation = new ProfilePagePictureOperation(getContext(), getActivity(), ProfileFragment.this, view, document, db,user);
                            initComponents(view);
                        }
                    } else {
                        Log.d(TAG, "No such user document");
                    }
                } else {
                    Log.d(TAG, "get task failed with ", task.getException());
                }
            }
        });
    }

    /**
     * The main function to initialize all function on this page.
     * @param view to fetch components on this page.
     */
    private void initComponents(View view) {
        adminBtn  = view.findViewById(R.id.btn_admmin);
        initAdminButton();
    }

    private void parseDocument() {
        if (document.get(General.ROLE) != null) {role = document.getLong(General.ROLE);}
    }

    private void initAdminButton() {
        if (!General.isAdmin(role)) {
            adminBtn.setEnabled(false);
            adminBtn.setVisibility(View.INVISIBLE);
            return;
        }
        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    General.toOtherActivity(getActivity(), AdminActivity.class);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profilePagePictureOperation.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        profilePagePictureOperation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}