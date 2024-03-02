package com.example.connectue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;



public class AddPostActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_STORAGE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //views
    EditText postDescription;
    Button publishPostBtn;

    ImageView postImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        postDescription = findViewById(R.id.postDescription);
        publishPostBtn = findViewById(R.id.publishPostBtn);
        postImage = findViewById(R.id.addPostImageBtn);

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });


        publishPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = postDescription.getText().toString().trim();
            }
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
    }

    private void pickImageFromGallery() {
    }
}