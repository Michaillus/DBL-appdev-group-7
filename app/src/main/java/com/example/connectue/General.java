package com.example.connectue;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class General {

    //    user role parameters in database
    public static final long ADMIN = 0;
    public static final long STUDENT = 1;
    public static final long GUEST = 2;

    //    the name of user collection, and the field names within
    public static final String USERCOLLECTION = "users";
    public static final String POSTCOLLECTION = "posts";
    public static final String REPORTEDCOLLECTION = "reported";
    public static final String PROFILECOLLECTION = "profilePicture";
//    fields name in "users"
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
//    field names in post
    public static final String PUBLISHER = "publisher";
//    field name in reported
    public static final String REPORTEDBYUSERS = "reportedBy";
    public static final String REPORTCOUNTER = "count";

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_STORAGE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final String TAG = "General";
    public static final String REPORTEDPROMPT = "Thanks for your report. Your report is being processing.";
    public static final String NONREPORTEDPROMPT = "Do you want to report the inappropriate content?";

    public static boolean isAdmin(long role) {
        return role == ADMIN;
    }
    public static boolean isStudent(long role) {
        return role == STUDENT;
    }
    public static boolean isGuest(long role) {
        return role != ADMIN && role!= STUDENT;
    }


    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser()
                == null ? "":FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static String getFileExtension(android.content.Context context, Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver()
                .getType(uri));
    }

    public static void reportOperation(@NonNull android.content.Context context,
                                       String collectionName, String contentId) {
        String uId = getUid();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(REPORTEDCOLLECTION);
        Query targetReport = collectionReference.whereEqualTo("contentId", contentId);
        targetReport.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
//                    isReportExist = result != null && result.size() > 0;
                    if (result != null && result.size() > 0) {
                        DocumentSnapshot lastDocument = result.getDocuments().get(result.size() - 1);
                        List<String> reportedList = toStringList(lastDocument.get(REPORTEDBYUSERS));
//                         isUserReported = reportedList.contains(uId);
                        String docId = lastDocument.getId();
                         if (reportedList.contains(uId)) {
//                            TODO: show exist popup window
                             createAlreadyReportedWindow(context);
                         } else {
//                             todo: update count and add users.
                             createReportWindow(context, uId, collectionName, contentId
                                     , true, collectionReference, reportedList, lastDocument);
                         }
                    } else {
//                        todo: add item directly
                        createReportWindow(context, uId, collectionName, contentId
                                , false, collectionReference, null, null);
                    }
                } else {
                    Log.i(TAG, "Query failed");
                }
            }
        });


    }

//    -----------helper function ---------
    private static void createAlreadyReportedWindow(@NonNull android.content.Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report").setMessage(REPORTEDPROMPT)
                .setPositiveButton("Goed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private static void createReportWindow(@NonNull android.content.Context context
            , String uId, String collectionName, String contentId, boolean isReportExist
            , CollectionReference reportCollection, List<String> reportedList
            , DocumentSnapshot document) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report");
        builder.setMessage(NONREPORTEDPROMPT);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isReportExist) {
                    updateReportedItem(uId, reportedList, reportCollection, document);
                } else {
                    createReportItem(uId, collectionName, contentId, reportCollection);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private static List<String> toStringList(Object object) {
        List<String> result = new ArrayList<>();
        if (object instanceof List) {
            for (Object item: (List<Object>) object) {
                result.add(item.toString());
            }
        }
        return result;
    }

    private static void createReportItem(String uId, String collectionName, String contentId
            , CollectionReference reportCollection){
        List<String> reportedUsers = new ArrayList<>();
        reportedUsers.add(uId);

        Map<String,Object> reportItem = new HashMap<>();
        reportItem.put("collectionName", collectionName);
        reportItem.put("contentId", contentId);
        reportItem.put("count", (long) 1);
        reportItem.put("reportedBy", reportedUsers);

        reportCollection.add(reportItem);
    }

    private static void updateReportedItem(String uId, List<String> reportedList
            , CollectionReference collectionReport, DocumentSnapshot document) {
        String docId = document.getId();
        int count = document.getLong(REPORTCOUNTER).intValue();
        Map<String, Object> updateData = new HashMap<>();

        reportedList.add(uId);
        count++;
        updateData.put(REPORTCOUNTER, count);
        updateData.put(REPORTEDBYUSERS, reportedList);

        collectionReport.document(docId).update(updateData);
    }

}
