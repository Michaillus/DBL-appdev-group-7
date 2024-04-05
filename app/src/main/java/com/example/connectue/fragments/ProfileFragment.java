package com.example.connectue.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.connectue.utils.General;
import com.example.connectue.R;
import com.example.connectue.activities.AdminActivity;
import com.example.connectue.utils.ProfilePageBasicInfo;
import com.example.connectue.utils.ProfilePageHistoryFunction;
import com.example.connectue.utils.ProfilePagePictureOperation;
import com.example.connectue.utils.ProfilePageSignoutDelete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    // to store the role of the user such that profile page can display different contents based on different user roles.
    private long role = 2;
    Button adminBtn;
    // user information display and interaction module.
    private ProfilePageBasicInfo basicInfo;
    // user sign out and delete account module.
    private ProfilePageSignoutDelete signoutDeleteModule;
    // user post history and course review function module.
    private ProfilePageHistoryFunction profilePageHistoryFunction;
    // user profile picture and the interaction on the picture module.
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

    /**
     * Initialization all components on the profile page. It will first fetch the
     *  data of the user, and then fill in each field, and display proper components
     *  based on the user data.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view, a view with buttons and profile pictures on this page.
     */
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
     *  all components on this page. To initialize the user information related components, the
     *  program crete an instance of BasicInfo; to initialize the signout and delete account button,
     *  the program create an instance of SignoutDeleteModule; to initialize the post history and
     *  course review history button, the instance of ProfilePageHistoryFunction will be created;
     *  to initialize the profile picture and add click listener on it, the instance of
     *  ProfilePagePictureOperation will be created.
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
     * The portal to initialize all components and functions on the profile page, except user infomation
     *  module, history module, sign out and delete account module, and the profile picture operation
     *  module.
     * @param view to fetch components on this page.
     */
    private void initComponents(View view) {
        adminBtn  = view.findViewById(R.id.btn_admmin);
        initAdminButton();
    }

    /**
     * To parse the user role using the instance of DocumentSnapshot.
     */
    private void parseDocument() {
        if (document.get(General.ROLE) != null) {role = document.getLong(General.ROLE);}
    }

    /**
     * Initialize the administrator mode button.
     */
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

    /**
     * Once the profile picture is updated, either through taking picture or choosing local pictures,
     *  this method capture the result and pass the result to the instance profilePagePictureOperation.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profilePagePictureOperation.onActivityResult(requestCode,resultCode,data);
    }

    /**
     * When the first time updating the profile picture, this function will capture the user
     *  choice, and pass the result to the instance of ProfilePagePictureOperation.
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        profilePagePictureOperation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}