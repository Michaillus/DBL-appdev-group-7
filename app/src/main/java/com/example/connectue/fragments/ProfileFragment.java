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
    private static final String LOGOUT = "Logout";
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_STORAGE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentSnapshot document;
    private String firstNameStr = "";
    private String lastNameStr = "";
    private String emailStr = "";
    private String phoneStr = "";
    private String spinnerStr = "";
    private String spinnerStr2 = "";
    private String imageURL = "";
    private Uri imageUri = null;
    private boolean isEditing = false;
    private boolean isAdmin = false;
    private long role = 2;
    Button editBtn;
    Button logoutBtn;
    Button deleteBtn;
    Button postHisBtn;
    Button reviewHisBtn;
    Button adminBtn;
    EditText firstName_fld;
    EditText lastName_fld;
    TextView majorTextView;
    EditText email_fld;
    EditText phone_fld;
    ImageView profileIV;
    Spinner majorSpinner;
    Spinner majorSpinner2;
    private ProfilePageBasicInfo basicInfo;

    public ProfileFragment() {
        // Required empty public constructor
        isEditing = false;
        isAdmin = false;
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
//        Log.i(TAG, "start executing crete view");
//        Log.i(TAG, "before: " + firstNameStr);
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
//        Log.i(TAG, "end executing onCreate, first name: " + firstNameStr);
    }

    /**
     * The main function to initialize all function on this page.
     * @param view to fetch components on this page.
     */
    private void initComponents(View view) {
        logoutBtn = view.findViewById(R.id.btn_logout);
        deleteBtn = view.findViewById(R.id.btn_deleteAccount);
        postHisBtn = view.findViewById(R.id.btn_postHistory);
        reviewHisBtn  = view.findViewById(R.id.btn_reviewHistory);
        adminBtn  = view.findViewById(R.id.btn_admmin);
        profileIV = view.findViewById(R.id.profilePic);

        if (General.isGuest(role)) {
            postHisBtn.setVisibility(View.GONE);
            reviewHisBtn.setVisibility(View.INVISIBLE);
        }

        initSignoutButton();
        initDeleteButton();
        initPostHisButton();
        initReviewHisButton();
        initAdminButton();
        initProfileImageView();
    }

    private void initDeleteButton() {
        deleteBtn.setText("DELETE ACCOUNT");

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signoutDeletePopup(false);
            }
        });
    }

    private void deleteUserContents() {
        CollectionReference reviewsRef = db.collection(General.COURSEREVIEWCOLLECTION);
        CollectionReference postsRef = db.collection(General.POSTCOLLECTION);
        CollectionReference commentsRef = db.collection(General.COMMENTCOLLECTION);
        deleteUserContents(reviewsRef);
        deleteUserContents(postsRef);
        deleteUserContents(commentsRef);
    }

    private void deleteUserContents(CollectionReference collectionRef) {

        collectionRef.whereEqualTo("publisher", user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // delete posts posted by this user
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getReference().delete();

                            // delete photos of the post that are still stored in storage
                            String photoURL = documentSnapshot.getString("photoURL");
                            if (photoURL != null){
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoURL);
                                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {}
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {}
                                });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {}
                });
    }

    private void initSignoutButton() {
        logoutBtn.setText(LOGOUT);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signoutDeletePopup(true);
            }
        });
    }

    private void signoutDeletePopup(boolean isSignout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (isSignout) {
            builder.setTitle("Logout");
            builder.setMessage("Do you want to sign out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    dialog.dismiss();
                    toOtherActivity(LoadingActivity.class);
                }
            });
        } else {
            builder.setTitle("Delete");
            builder.setMessage("ARE YOU SURE YOU WANT TO DELETE?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteUserContents();

                    db.collection(General.USERCOLLECTION).document(user.getUid()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseAuth.getInstance().signOut();
                                            toOtherActivity(LoadingActivity.class);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Delete failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    private void toOtherActivity(Class activity) {
        Intent loading = new Intent(getActivity(), activity);
        getActivity().startActivity(loading);
//        getActivity().finish();
    }

    private void parseDocument() {
        emailStr = document.getString(General.EMAIL) == null ? "": document.getString(General.EMAIL);
        imageURL = document.getString(General.PROFILEPICTURE) == null ? "": document.getString(General.PROFILEPICTURE);
        if (document.get(General.ROLE) != null) {role = document.getLong(General.ROLE);}
    }

    private void initPostHisButton() {
        postHisBtn.setText("Post History");
        postHisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history = new Intent(getActivity(),PostHistoryActivity.class);
                history.putExtra("collection", General.POSTCOLLECTION);
//                history.putExtra("collection", General.COURSEREVIEWCOLLECTION);
                getActivity().startActivity(history);
            }
        });
    }

    private void initReviewHisButton() {
        reviewHisBtn.setText("Course Review History");
        reviewHisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history = new Intent(getActivity(),PostHistoryActivity.class);
                history.putExtra("collection", General.COURSEREVIEWCOLLECTION);
                getActivity().startActivity(history);
            }
        });
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
                toOtherActivity(AdminActivity.class);
            }
        });
    }

    private void initProfileImageView() {

//        if (General.isGuest(role)) {
//            return;
//        }

        if (imageURL != null && !imageURL.equals("")) {
            Glide.with(getContext()).load(imageURL).into(profileIV);
        }

        profileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureOperation();
            }
        });
    }

    private void pictureOperation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Image");
        builder.setItems(new CharSequence[]{"Pick from Gallery", "Capture from Camera"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    chooseLocalPicture();
                    break;
                case 1:
                    takePicture();
                    break;
            }
        });
        builder.show();
    }

    private void chooseLocalPicture() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO}, REQUEST_STORAGE);
            } else {
                // Permission already granted, proceed with gallery operation
                pickImageFromGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE);
                Log.d(TAG, "requestStoragePermission: requestStoragePermission");
            } else {
                // Permission already granted, proceed with gallery operation
                pickImageFromGallery();
            }
        }
    }

    private void pickImageFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        } else {
            // Permission already granted, proceed with camera operation
            captureImageFromCamera();
        }
    }

    private void captureImageFromCamera() {
        //intent to pick image from camera
        Log.i(TAG_Profile, "captureImageFromCamera entered ");
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG_Profile, "activity result entered ");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image picked from gallery
                imageUri = data.getData();
            }
            profileIV.setImageURI(imageUri);
            profileIV.setVisibility(View.VISIBLE);
            updateProfilePicture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with camera operation
                captureImageFromCamera();
            } else {
                // Camera permission denied, handle accordingly (e.g., show explanation, disable camera feature)
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission granted, proceed with gallery operation
                pickImageFromGallery();
            } else {
                // Storage permission denied, handle accordingly (e.g., show explanation, disable gallery feature)
                Toast.makeText(getContext(), "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateProfilePicture() {
        Log.i(TAG_Profile, "updateProfilePicture entered");
        if (imageUri == null) {
            return;
        }

        if (imageURL != null && !imageURL.equals("")) {
            StorageReference currentPicture = FirebaseStorage.getInstance().
                    getReferenceFromUrl(imageURL);
            currentPicture.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i(TAG_Profile, "delete success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG_Profile, "delete fail");
                        }
                    });
        }

        StorageReference filePath = FirebaseStorage.getInstance().getReference("profilePicture")
                .child(emailStr + "." + General.getFileExtension(getContext(), imageUri));

        filePath.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //        TODO: update the profile URL in user data
                                        String downLoadURL = uri.toString();
                                        db.collection(General.USERCOLLECTION).document(user.getUid()).update(PROFILEPICTURE, downLoadURL);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(),
                                                "Failed to upload", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG_Profile, "In updateProfilePicture, upload to storage failed");
                    }
                });
        Log.i(TAG_Profile, "In updateProfilePicture, after upload");
    }
}