package com.example.connectue.utils;

import static com.example.connectue.utils.General.PROFILEPICTURE;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.connectue.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * The profile picture module. This class will add click listener to the profile picture such that
 *  users can either choose the local picture or activating camera to take a picture as their profile
 *  pictures and upload to the Firestore.
 */
public class ProfilePagePictureOperation {
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_STORAGE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final String TAG = "Profile";
    private static final String TAG_Profile = "ProfilePic";
    private final Context context;
    private final Activity activity;
    private final androidx.fragment.app.Fragment fragment;
    private final View view;
    private DocumentSnapshot document;
    private ImageView profileIV;

    /**
     * Getter methods for the the context this class will be called.
     */
    public Context getContext() {
        return context;
    }

    /**
     * getter method for the activity.
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * getter method for the fragment the class will be called.
     */
    public Fragment getFragment() {
        return fragment;
    }

    /**
     * getter method for the view the class will be in.
     */
    public View getView() {
        return view;
    }

    /**
     * Getter and setter methods for variables that are not final.
     */
    public DocumentSnapshot getDocument() {
        return document;
    }

    /**
     * setter method for document snapshot.
     */
    public void setDocument(DocumentSnapshot document) {
        this.document = document;
    }

    /**
     * getter method for profile.
     */
    public ImageView getProfileIV() {
        return profileIV;
    }

    /**
     * setter method for profile.
     */
    public void setProfileIV(ImageView profileIV) {
        this.profileIV = profileIV;
    }

    /**
     * getter method for database.
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * getter method for user reference.
     */
    public FirebaseUser getUser() {
        return user;
    }

    /**
     * getter method for the image URL.
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * setter method for the image URL.
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * getter method for the image Uri.
     */
    public Uri getImageUri() {
        return imageUri;
    }

    /**
     * setter method for the image Uri.
     */
    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    /**
     * getter method for Email that the user used.
     */
    public String getEmailStr() {
        return emailStr;
    }

    /**
     * setter method for Email that the user used.
     */
    public void setEmailStr(String emailStr) {
        this.emailStr = emailStr;
    }

    private final FirebaseFirestore db;
    private final FirebaseUser user;
    private String imageURL = "";
    Uri imageUri = null;
    private String emailStr = "";

    /**
     * Constructor.
     * @param context the context this class will be called.
     * @param activity the activity this class will be called.
     * @param fragment the fragment this class will be called
     * @param view the view this class will be in.
     * @param document the DocumentSnapshot storing the user information.
     * @param db the reference of the database.
     * @param user the reference of the user.
     */
    public ProfilePagePictureOperation(Context context, Activity activity,  androidx.fragment.app.Fragment fragment,
                                       View view, DocumentSnapshot document, FirebaseFirestore db,
                                       FirebaseUser user) {
        this.context = context;
        this.activity = activity;
        this.fragment = fragment;
        this.view = view;
        this.document = document;
        this.db = db;
        this.user = user;
        parseDocument();
        if(context != null) {
            initProfileImageView();
        }
    }

    /**
     * Parse the email address and existing image URL address.
     */
    private void parseDocument() {
        if (document != null) {
            emailStr = document.getString(General.EMAIL) == null ? "": document.getString(General.EMAIL);
            imageURL = document.getString(General.PROFILEPICTURE) == null ? "": document.getString(General.PROFILEPICTURE);
        }
    }

    /**
     * Initialize image view for the profile picture and fetch the picture stored in the Firestorage.
     */
    private void initProfileImageView() {
        profileIV = view.findViewById(R.id.profilePic);

        if (imageURL != null && !imageURL.equals("")) {
            Glide.with(context).load(imageURL).into(profileIV);
        }

        profileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureOperation();
            }
        });
    }

    /**
     * Show a popup window to let user choose where to take the profile pictures.
     */
    private void pictureOperation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    /**
     * Choose picture from the local device. If the access right is not granted ask the user to grant.
     */
    private void chooseLocalPicture() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO}, REQUEST_STORAGE);
            } else {
                // Permission already granted, proceed with gallery operation
                pickImageFromGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE);
//                Log.d(TAG, "requestStoragePermission: requestStoragePermission");
            } else {
                // Permission already granted, proceed with gallery operation
                pickImageFromGallery();
            }
        }
    }

    /**
     * Access to the gallery of the local device.
     */
    private void pickImageFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    /**
     * Take profile picture from the camera. If the access right is not granted ask the user to grant.
     */
    private void takePicture() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        } else {
            // Permission already granted, proceed with camera operation
            captureImageFromCamera();
        }
    }

    /**
     * Activate the camera and take pictures.
     */
    private void captureImageFromCamera() {
        //intent to pick image from camera
//        Log.i(TAG_Profile, "captureImageFromCamera entered ");
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        fragment.startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    /**
     * Get the picture result and load it into ImageView. Then call updateProfilePicture to upload
     * pictures to the FireStorage.
     * @param requestCode the code to identify if the result is from camera or the gallery.
     * @param resultCode to show the operation succeed or failed.
     * @param data Intent storing image uri.
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    /**
     * Delete the profile picture in the FireStorage and update the new profile picture.
     */
    private void updateProfilePicture() {
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
                .child(emailStr + "." + General.getFileExtension(context, imageUri));

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
                                        Toast.makeText(activity,
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

    /**
     * Get user grant result.
     * @param requestCode to distinguish camera and gallery.
     * @param permissions list of Strings.
     * @param grantResults a list of grant result.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with camera operation
                captureImageFromCamera();
            } else {
                // Camera permission denied, handle accordingly (e.g., show explanation, disable camera feature)
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission granted, proceed with gallery operation
                pickImageFromGallery();
            } else {
                // Storage permission denied, handle accordingly (e.g., show explanation, disable gallery feature)
                Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
