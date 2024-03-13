package com.example.connectue;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Map;

public class General {

    //    user role parameters in database
    public static final long ADMIN = 0;
    public static final long STUDENT = 1;
    public static final long GUEST = 2;

    //    the name of user collection, and the field names within
    public static final String USERCOLLECTION = "users";
    public static final String EMAIL = "email";
    public static final String FIRSTNAME = "firstName";
    public static final String VERIFY = "isVerified";
    public static final String LASTNAME = "lastName";
    public static final String PASSWORD = "password";
    public static final String POSTHISTORY = "postHistory";
    public static final String PROFILEPICTURE = "profilePicURL";
    public static final String PROGRAM = "program";
    public static final String REVIEWHISTORY = "reviewHistory";
    public static final String ROLE = "role";
    public static final String COURSE = "userCourses";
    public static final String USERID = "userId";
    public static final String PHONE = "phone";
    public static final String POSTCOLLECTION = "posts";
    public static final String PUBLISHER = "publisher";
    public static final String PROFILECOLLECTION = "profilePicture";
    public static final String STORAGE_PROFILE_PICTURE = "profilePicture";
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_STORAGE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    public static boolean isAdmin(long role) {
        return role == ADMIN;
    }

    public static boolean isStudent(long role) {
        return role == STUDENT;
    }

    public static boolean isGuest(long role) {
        return role != ADMIN && role!= STUDENT;
    }

    public static String getFileExtension(android.content.Context context, Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver()
                .getType(uri));
    }

    public static void pictureOperation(@NonNull android.content.Context context,
        @NonNull android.app.Activity activity, Uri imageUri, Map<String, Uri> userData){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Image");
        builder.setItems(new CharSequence[]{"Pick from Gallery", "Capture from Camera"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    chooseLocalPicture(context, activity,imageUri);
                    break;
                case 1:
                    takePicture(context, activity, userData);
                    break;
            }
        });
        builder.show();
    }

    public static void requestPicturePermission(int requestCode, @NonNull int[] grantResults
            , @NonNull android.content.Context context, @NonNull android.app.Activity activity
            , Map<String, Uri> userData) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with camera operation
                captureImageFromCamera(context, activity, userData);
            } else {
                // Camera permission denied, handle accordingly (e.g., show explanation, disable camera feature)
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission granted, proceed with gallery operation
                pickImageFromGallery(activity);
            } else {
                // Storage permission denied, handle accordingly (e.g., show explanation, disable gallery feature)
                Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void afterPictureOperation(ImageView imageView, Uri imageUri, @Nullable Intent data,
                                             int requestCode, int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                imageUri = data.getData();
            }
        }
        imageView.setImageURI(imageUri);
        imageView.setVisibility(View.VISIBLE);
    }

//    ---------------helper functions
    private static void takePicture(@NonNull android.content.Context context,
        @NonNull android.app.Activity activity, Map<String,Uri> userData) {

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        } else {
            // Permission already granted, proceed with camera operation
            captureImageFromCamera(context,activity, userData);
        }
    }

    private static void captureImageFromCamera(@NonNull android.content.Context context,
            @NonNull android.app.Activity activity, Map<String,Uri> userData) {
        //intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        userData.put(General.PROFILEPICTURE, imageUri);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private static void chooseLocalPicture(@NonNull android.content.Context context
            , @NonNull android.app.Activity activity, Uri imageUri) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        } else {
            pickImageFromGallery(activity);
        }
    }

    private static void pickImageFromGallery(@NonNull android.app.Activity activity){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

//    ----------------------------------
//public static void pictureOperation(@NonNull android.content.Context context,
//                                    @NonNull android.app.Activity activity, Uri imageUri){
//    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//    builder.setTitle("Add Image");
//    builder.setItems(new CharSequence[]{"Pick from Gallery", "Capture from Camera"}, (dialog, which) -> {
//        switch (which) {
//            case 0:
//                chooseLocalPicture(context, activity,imageUri);
//                break;
//            case 1:
//                takePicture(context, activity, imageUri);
//                break;
//        }
//    });
//    builder.show();
//}
//
//    public static void requestPicturePermission(int requestCode, @NonNull int[] grantResults
//            , @NonNull android.content.Context context, @NonNull android.app.Activity activity
//            , Uri imageUri) {
//        if (requestCode == REQUEST_CAMERA) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Camera permission granted, proceed with camera operation
//                captureImageFromCamera(context, activity, imageUri);
//            } else {
//                // Camera permission denied, handle accordingly (e.g., show explanation, disable camera feature)
//                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show();
//            }
//        } else if (requestCode == REQUEST_STORAGE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Storage permission granted, proceed with gallery operation
//                pickImageFromGallery(activity);
//            } else {
//                // Storage permission denied, handle accordingly (e.g., show explanation, disable gallery feature)
//                Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    public static void afterPictureOperation(ImageView imageView, Uri imageUri, @Nullable Intent data,
//                                             int requestCode, int resultCode) {
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
//                imageUri = data.getData();
//            }
//        }
//        imageView.setImageURI(imageUri);
//        imageView.setVisibility(View.VISIBLE);
//    }
//
//    //    ---------------helper functions
//    private static void takePicture(@NonNull android.content.Context context,
//                                    @NonNull android.app.Activity activity, Uri imageUri) {
//        boolean grand_permission = false;
//        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
//                    REQUEST_CAMERA);
//        } else {
//            // Permission already granted, proceed with camera operation
//            captureImageFromCamera(context,activity, imageUri);
//        }
//    }
//
//    private static void captureImageFromCamera(@NonNull android.content.Context context,
//                                               @NonNull android.app.Activity activity, Uri imageUri) {
//        //intent to pick image from camera
//        ContentValues cv = new ContentValues();
//        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
//        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
//        imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        activity.startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
//
//    }
//
//    private static void chooseLocalPicture(@NonNull android.content.Context context
//            , @NonNull android.app.Activity activity, Uri imageUri) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    REQUEST_STORAGE);
//        } else {
//            pickImageFromGallery(activity);
//        }
//    }
//
//    private static void pickImageFromGallery(@NonNull android.app.Activity activity){
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        activity.startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
//    }
}
