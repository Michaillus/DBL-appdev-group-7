package com.example.connectue.utils;

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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
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
    public static final String COURSEREVIEWCOLLECTION = "course-reviews";
    public static final String COMMENTCOLLECTION = "post-comments";

//    fields name in "users"
    public static final String EMAIL = "email";
    public static final String FIRSTNAME = "firstName";
    public static final String VERIFY = "isVerified";
    public static final String LASTNAME = "lastName";
    public static final String PROFILEPICTURE = "profilePicURL";
    public static final String PROGRAM = "program";
    public static final String ROLE = "role";
    public static final String COURSE = "userCourses";
    public static final String USERID = "userId";
    public static final String PHONE = "phone";
//    field names in post
    public static final String POSTIMAGEURL = "photoURL";
    public static final String PUBLISHER = "publisher";
    public static final String POSTCONTENT = "text";
//    field name in reported
    public static final String REPORTFROMCOLLECTION = "collectionName";
    public static final String REPORTCONTENTID = "contentId";
    public static final String REPORTCOUNTER = "count";
    public static final String REPORTEDBYUSERS = "reportedBy";
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

    /**
     * To return the user's id in the Firebase authentication.
     * @return the user id in the Firebase authentication.
     */
    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser()
                == null ? "":FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * To return the extension of the file.
     * @param context the current context of the activity.
     * @param uri the instance of the Uri class.
     * @return the extension of the file.
     */
    public static String getFileExtension(android.content.Context context, Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver()
                .getType(uri));
    }

    /**
     * Report an inappropriate content. It creates a popup window to ask users to if he really wants
     * to report this content. If so, then it will generate a new document in the Firebase Firestore
     * reported collection indicating which collection this content is in, its document id, and who
     * reported this content.
     * @param context the context where the popup window will show.
     * @param collectionName which collection this content belongs to in the Firestore.
     * @param contentId the id of the content.
     */
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
//                             show exist popup window
                             createAlreadyReportedWindow(context);
                         } else {
//                              update count and add users.
                             createReportWindow(context, uId, collectionName, contentId
                                     , true, collectionReference, reportedList, lastDocument);
                         }
                    } else {
//                         add item directly
                        createReportWindow(context, uId, collectionName, contentId
                                , false, collectionReference, null, null);
                    }
                } else {
                    Log.i(TAG, "Query failed");
                }
            }
        });
    }

    /**
     * Show the full size image. Use for e.g. image of a post, comment.
     * @param v imageView of the image
     */
    public static void showPopupWindow(View v, String imageUrl) {
        ImageView fullImageView = new ImageView(v.getContext());
        fullImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fullImageView.setPadding(8,0,8,0);
        fullImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        fullImageView.setFocusable(true);

        // Load the full-size image into the ImageView
        Glide.with(v.getContext())
                .load(imageUrl) // Load the same image URL
                .into(fullImageView);

        // Create a popup window to display the full-size image
        PopupWindow popupWindow = new PopupWindow(fullImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        // Close the popup window if the full imageview is clicked.
        fullImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * Given the original activity, and the target activity, the target activity will be activated from
     *  the original activity.
     * @param originalActivity the starting activity.
     * @param newActivity the target activity.
     */
    public static void toOtherActivity(@NonNull Activity originalActivity, Class newActivity) {
        Intent loading = new Intent(originalActivity, newActivity);
        originalActivity.startActivity(loading);
    }

//    -----------helper function ---------

    /**
     * Create a popup window to indicate that this user has reported this content already.
     * @param context the context where the popup window will show.
     */
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

    /**
     * Helper function. To create a popup window when this user did not report this content before.
     * @param context the context where the popup window will show.
     * @param uId the id of the user
     * @param collectionName the collection this content belongs to in the Firestore.
     * @param contentId the id of the content.
     * @param isReportExist if the report document of this content already exist.
     * @param reportCollection the collection reference to the report collection in the Firestore.
     * @param reportedList the list of users reported this content.
     * @param document if the report document already exists, pass in its instance of DocumentSnapshot
     *                 for updating, otherwise pass in null.
     */
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

    /**
     * Convert an object to a list of String.
     * @param object the object
     * @return a list of string.
     */
    public static List<String> toStringList(Object object) {
        List<String> result = new ArrayList<>();
        if (object instanceof List) {
            for (Object item: (List<Object>) object) {
                result.add(item.toString());
            }
        }
        return result;
    }

    /**
     * Create a new report document, and add it to the report collection.
     * @param uId user id.
     * @param collectionName the collection name that the content belongs to.
     * @param contentId the id of the content.
     * @param reportCollection the reference to the report collection.
     */
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

    /**
     * Push the reported content to Firestore.
     * @param uId user id.
     * @param reportedList list of users reported this content.
     * @param collectionReport the reference to the report collection.
     * @param document if the report document already exists, pass in its instance of DocumentSnapshot
     *      *          for updating, otherwise pass in null.
     */
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
