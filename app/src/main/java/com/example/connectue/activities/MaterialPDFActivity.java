package com.example.connectue.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.connectue.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Allow users to download pdf files uploaded by others (or themselves for that matter).
 */
public class MaterialPDFActivity extends AppCompatActivity {

    /**
     * attribute to store the url of the pdf globally
     */
    String docURL;
    /**
     * attribute to point to the download button
     * displayed on the activity.
     */
    ExtendedFloatingActionButton downloadButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_pdfactivity);
        //Assign the download button to the attribute
        downloadButton = findViewById(R.id.downloadBtn);

        /**
         * Get the URL of the pdf from the previous activity
         * as the url is specific per material item posted.
         */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras == null) {
                docURL= "";
            } else {
                docURL= extras.getString("docURL");
            }
        } else {
            docURL= (String) savedInstanceState.getSerializable("docURL");
        }

        /**
         * listener for when the download button is clicked.
         */
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile(docURL);
            }
        });
    }


    /**
     * Private helper method to initiate android download process
     * @param url the firebase storage url of the pdf to download.
     */
    private void downloadFile(String url) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url);

        //get file path to downloads directory in android
        File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,"download.pdf");
        /**
         * get the file from the database using the url argument in storageRef.
         */
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            /**
             * When upload is successful, close activity to return to the previous one.
             * @param taskSnapshot snapshot of the download task.
             */
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local tem file created  created " +localFile.toString());
                Toast.makeText( MaterialPDFActivity.this,"File successfully downloaded", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            /**
             * When upload is unsuccessful, notify user of issue.
             * @param exception the exception that occurred when trying to download.
             */
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText( MaterialPDFActivity.this,"An issue occurred, try again.", Toast.LENGTH_SHORT).show();
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        });
    }
}