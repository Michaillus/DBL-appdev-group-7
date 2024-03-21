package com.example.connectue.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.connectue.R;
import com.example.connectue.managers.PostManager;
import com.example.connectue.interfaces.FireStoreUploadCallback;
import com.example.connectue.model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity class: ";

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_STORAGE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    // FireBase posts collection manager.
    PostManager postManager;

    //views
    EditText postDescription;
    Button publishPostBtn;
    ImageView addImageBtn;
    ImageView postImage;

    //Reference to the Cloud Firestore database
    CollectionReference posts;

    //image picked will be saved in this uri
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        posts = FirebaseFirestore.getInstance().collection("posts");

        // Initializing post manager
        postManager = new PostManager(FirebaseFirestore.getInstance(),
                Post.POST_COLLECTION_NAME, Post.POST_LIKE_COLLECTION_NAME,
                Post.POST_DISLIKE_COLLECTION_NAME, Post.POST_COMMENT_COLLECTION_NAME);

        postDescription = findViewById(R.id.postDescription);
        publishPostBtn = findViewById(R.id.publishPostBtn);
        addImageBtn = findViewById(R.id.addPostImageBtn);
        postImage = findViewById(R.id.postImage);

        addImageBtn.setOnClickListener(v -> showImagePickDialog());


        publishPostBtn.setOnClickListener(v -> {
            publishPostBtn.setEnabled(false);
            postDescription.setEnabled(false);
            addImageBtn.setEnabled(false);
            String description = postDescription.getText().toString().trim();
            publishPost(description, imageUri);
        });
    }

    private void showImagePickDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");
        builder.setItems(new CharSequence[]{"Pick from Gallery", "Capture from Camera"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    requestStoragePermission();
                    break;
                case 1:
                    requestCameraPermission();
                    break;
            }
        });
        builder.show();
    }

    // Method to check and request camera permission
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        } else {
            // Permission already granted, proceed with camera operation
            captureImageFromCamera();
        }
    }

    // Method to check and request storage permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        } else {
            // Permission already granted, proceed with gallery operation
            pickImageFromGallery();
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with camera operation
                captureImageFromCamera();
            } else {
                // Camera permission denied, handle accordingly (e.g., show explanation, disable camera feature)
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission granted, proceed with gallery operation
                pickImageFromGallery();
            } else {
                // Storage permission denied, handle accordingly (e.g., show explanation, disable gallery feature)
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void captureImageFromCamera() {
        //intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private void pickImageFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    // Update UI after selecting an image.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //image picked from camera
                postImage.setImageURI(imageUri);
                postImage.setVisibility(View.VISIBLE);
            }
            else if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image picked from gallery
                imageUri = data.getData();

                postImage.setImageURI(imageUri);
                postImage.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Uploads image and a post to the database.
     * Post can be without an image.
     * @param text text of the post
     * @param imageUri identifier of the post image on the device or null if the post doesn't
     *                 have an image.
     */
    private void publishPost(String text, Uri imageUri) {
        Log.i("Upload file", "Start uploading the file");

        if (imageUri != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.GERMANY);
            Date now = new Date();
            String fileName = formatter.format(now);

            StorageReference filePath = FirebaseStorage.getInstance().getReference("posts")
                    .child(fileName + "." + getFileExtension(imageUri));

            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i("Upload file", "File is successfully uploaded");
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.i("Upload file",
                                            "url to the file is successfully obtained");
                                    String imageUrl = uri.toString();
                                    uploadPostToDatabase(text, imageUrl);
                                }
                            }).addOnFailureListener(e -> {
                                Log.e("Upload file",
                                        "Failed to get image url: " + e.getMessage());
                                Toast.makeText(AddPostActivity.this,
                                        "Failed to upload", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("Upload file",
                                "Failed to upload image: " + e.getMessage());
                        Log.e("Upload file", imageUri.toString());
                        Toast.makeText(AddPostActivity.this,
                                "Failed to upload", Toast.LENGTH_SHORT).show();
                    });
        } else {
            uploadPostToDatabase(text, null);
        }
    }

    /**
     * Uploads post to the database with the given text and link to an image.
     * Post can be without an image.
     * @param text text of the post
     * @param imageUrl link to an image stored in the database or null if the post doesn't have
     *                 an image
     */
    private void uploadPostToDatabase(String text, String imageUrl) {
        String publisherId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Post post = new Post(publisherId, text, imageUrl);
        postManager.upload(post, new FireStoreUploadCallback() {
            @Override
            public void onSuccess() {
                Log.i("Upload post", "Post is uploaded successfully");
                Toast.makeText(AddPostActivity.this,
                        "Post is published successfully", Toast.LENGTH_SHORT).show();

                Intent intentPosts = new Intent(AddPostActivity.this, MainActivity.class);
                startActivity(intentPosts);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Upload post", "Failed to upload the post: " + e.getMessage());
                Toast.makeText(AddPostActivity.this,
                        "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Get extension of a file from it's path on the device.
     * @param uri identifier of the file on the device
     * @return extension of the file
     */
    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver()
                .getType(uri));
    }
}