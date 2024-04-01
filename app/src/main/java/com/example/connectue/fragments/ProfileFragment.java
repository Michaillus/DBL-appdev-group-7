package com.example.connectue.fragments;

import static com.example.connectue.utils.General.PROFILEPICTURE;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private static final String EDITON = "Save";
    private static final String EDITOFF = "Edit";
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

        Log.i(TAG, "start executing onCreate");
//        db = FirebaseFirestore.getInstance();
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        Log.i(TAG, user.getUid());
//        DocumentReference documentReference = db.collection(General.USERCOLLECTION).document(user.getUid());
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//              @Override
//              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                  if (task.isSuccessful()) {
//                      document = task.getResult();
//
//                      if (document.exists()) {
//                          Log.i(TAG,"document fetched");
//                          Log.i(TAG, document.toString());
//                          parseDocument();
//                          updateUIComponents();
//                      } else {
//                          Log.d(TAG, "No such user document");
//                      }
//                  } else {
//                      Log.d(TAG, "get task failed with ", task.getException());
//                  }
//              }
//        });
//        Log.i(TAG, "end executing onCreate, first name: " + firstNameStr);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.i(TAG, "start executing crete view");
        Log.i(TAG, "before: " + firstNameStr);
        initData(view);
//        initComponents(view);
        return view;
    }

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
                            initComponents(view);
                        }
//                        initComponents(view);
                    } else {
                        Log.d(TAG, "No such user document");
                    }
                } else {
                    Log.d(TAG, "get task failed with ", task.getException());
                }
            }
        });
        Log.i(TAG, "end executing onCreate, first name: " + firstNameStr);
    }

    private void initComponents(View view) {
        firstName_fld = view.findViewById(R.id.text_firstName);
        lastName_fld = view.findViewById(R.id.text_lastName);
        email_fld = view.findViewById(R.id.text_email);
        phone_fld = view.findViewById(R.id.text_phone);

        editBtn= view.findViewById(R.id.btn_edit);
        logoutBtn = view.findViewById(R.id.btn_logout);
        deleteBtn = view.findViewById(R.id.btn_deleteAccount);
        postHisBtn = view.findViewById(R.id.btn_postHistory);
        reviewHisBtn  = view.findViewById(R.id.btn_reviewHistory);
        adminBtn  = view.findViewById(R.id.btn_admmin);
        profileIV = view.findViewById(R.id.profilePic);
        majorSpinner = view.findViewById(R.id.majorSpinner);
        majorSpinner2 = view.findViewById(R.id.majorSpinner2);
        majorTextView = view.findViewById(R.id.major_title);

        majorSpinner.setEnabled(false);
        majorSpinner2.setEnabled(false);

        if (General.isGuest(role)) {
            postHisBtn.setVisibility(View.GONE);
            reviewHisBtn.setVisibility(View.INVISIBLE);
            majorTextView.setVisibility(View.INVISIBLE);
            majorSpinner.setVisibility(View.INVISIBLE);
            majorSpinner2.setVisibility(View.INVISIBLE);
        }

        initSpinner();
        initTextSection(view);
        initEditButton();
        initSignoutButton();
        initDeleteButton();
        initPostHisButton();
        initReviewHisButton();
        initAdminButton();
        initProfileImageView();
    }

    private void initSpinner() {
        if (getContext() != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    getContext(),
                    R.array.programs_array,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            majorSpinner.setAdapter(adapter);
            majorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            majorSpinner2.setAdapter(adapter);
            majorSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }


    }

    private void initTextSection(View view) {
        setTestFields();
        switchTextFields(isEditing);
    }

    private void setTestFields() {
        firstName_fld.setText(firstNameStr);
        lastName_fld.setText(lastNameStr);
        email_fld.setText(emailStr);
        phone_fld.setText(phoneStr);

        if (getContext() != null) {
            String[] items = getResources().getStringArray(R.array.programs_array);
            setSpinner(items, spinnerStr, majorSpinner);
            setSpinner(items, spinnerStr2, majorSpinner2);
        }
//        String[] items = getResources().getStringArray(R.array.programs_array);
//        setSpinner(items, spinnerStr, majorSpinner);
//        setSpinner(items, spinnerStr2, majorSpinner2);
    }

    private void setSpinner(String[] items, String spinnerString, Spinner spinner) {
        int position = -1;
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(spinnerString)) {
                position = i;
                break;
            }
        }

        if (position != -1) {
            spinner.setSelection(position);
        } else {}
    }

    private void switchTextFields(boolean onOff) {
        firstName_fld.setEnabled(onOff);
        lastName_fld.setEnabled(onOff);
        email_fld.setEnabled(onOff);
        phone_fld.setEnabled(onOff);
    }

    private void updateUIComponents() {
        if (getContext() != null) {
            setTestFields();
            if (!isAdmin) {
                adminBtn.setEnabled(false);
                adminBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initEditButton() {
        editBtn.setText(EDITOFF);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing) {
                    editBtn.setText(EDITOFF);
                    updateInfo();
                } else {
                    editBtn.setText(EDITON);
                }

                isEditing = !isEditing;
                switchTextFields(isEditing);
                setTestFields();

                majorSpinner.setEnabled(!majorSpinner.isEnabled());
                majorSpinner2.setEnabled(!majorSpinner2.isEnabled());
            }
        });

    }

    private void initDeleteButton() {
        deleteBtn.setText("DELETE ACCOUNT");

        CollectionReference reviewsRef = db.collection("reviews");
        CollectionReference postsRef = db.collection("posts");
        CollectionReference commentsRef = db.collection("post-comments");

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Firstly, delete all posts and reviews posted by this user
                deleteUserContents(reviewsRef);
                deleteUserContents(postsRef);
                deleteUserContents(commentsRef);

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


    private void updateInfo() {
        Map<String, Object> uploadInfo = new HashMap<>();
        firstNameStr = firstName_fld.getText().toString().trim();
        lastNameStr = lastName_fld.getText().toString().trim();
        emailStr = email_fld.getText().toString().trim();
        phoneStr = phone_fld.getText().toString().trim();
        spinnerStr = (String) majorSpinner.getSelectedItem();
        spinnerStr2 = (String) majorSpinner2.getSelectedItem();

        uploadInfo.put(General.FIRSTNAME, firstNameStr);
        uploadInfo.put(General.LASTNAME, lastNameStr);
        uploadInfo.put(General.PROGRAM, spinnerStr + " " + spinnerStr2);
        uploadInfo.put(General.EMAIL, emailStr);
        uploadInfo.put(General.PHONE, phoneStr);

        db.collection(General.USERCOLLECTION).document(user.getUid()).update(uploadInfo);
    }

    private void initSignoutButton() {
        logoutBtn.setText(LOGOUT);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                toOtherActivity(LoadingActivity.class);
            }
        });
    }

    private void toOtherActivity(Class activity) {
        Intent loading = new Intent(getActivity(), activity);
        getActivity().startActivity(loading);
//        getActivity().finish();
    }

    private void parseDocument() {
        if (document.get(General.FIRSTNAME) != null) {
            Log.i(TAG, "document-first name not null");
            Log.i(TAG, "before: " + firstNameStr);
            firstNameStr = (String) document.get(General.FIRSTNAME);
            Log.i(TAG,"after: " + firstNameStr);
        }
        if (document.get(General.LASTNAME) != null) {  lastNameStr = (String) document.get(General.LASTNAME);}
        if (document.get(General.EMAIL) != null) { emailStr = (String) document.get(General.EMAIL);}

        if (document.get(General.PROGRAM) != null) {
            String programString = (String) document.get(General.PROGRAM);
            String[] majors = programString.split(" ");

//            Log.i("print", majors[0] + majors[1]);
            if(majors == null || majors.length == 0) {
                spinnerStr = " ";
                spinnerStr2 = " ";
            } else if (majors.length == 1) {
                spinnerStr = majors[0];
                spinnerStr2 = " ";
            } else {
                spinnerStr = majors[0];
                spinnerStr2 = majors[1];
            }
        }

        if (document.get(General.PHONE) != null) { phoneStr = (String) document.get(General.PHONE);}
        if (document.get(General.LASTNAME) != null) {  lastNameStr = document.getString(General.LASTNAME);}
        if (document.get(General.EMAIL) != null) { emailStr = document.getString(General.EMAIL);}

        if (document.get(General.PHONE) != null) { phoneStr = document.getString(General.PHONE);}
        if (document.get(General.ROLE) != null) {
            role = document.getLong(General.ROLE);
//            isAdmin = General.isAdmin(document.getLong(General.ROLE));
//            Log.i(TAG, "" + isAdmin);
        }
        if (document.get(PROFILEPICTURE) != null) {
            imageURL = document.getString(PROFILEPICTURE);
        }
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

        if (General.isGuest(role)) {
            return;
        }

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