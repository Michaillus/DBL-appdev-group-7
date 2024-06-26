package com.example.connectue.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connectue.R;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.managers.MaterialManager;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.model.Material;
import com.example.connectue.utils.ActivityUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMaterialActivity extends AppCompatActivity {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "AddMaterialActivity";

    private static final int REQUEST_PICK_PDF_FILE = 1;

    /**
     * Course for which material is added.
     */
    private StudyUnit course;

    private FirebaseFirestore db;
    private Uri selectedPdfUri;

    TextInputEditText caption;
    MaterialManager materialManager;

    Boolean uploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_upload);

        //Get the instance of firebase
        db = FirebaseFirestore.getInstance();

        TextView title = findViewById(R.id.courseName);
        MaterialCardView uploadBtn = findViewById(R.id.addQuestionBtn);
        ExtendedFloatingActionButton submitBtn = findViewById(R.id.submitBtn);
        caption = findViewById(R.id.materialCaption);

        materialManager = new MaterialManager(db,
                Material.MATERIAL_COLLECTION_NAME,
                Material.MATERIAL_LIKE_COLLECTION_NAME,
                Material.MATERIAL_DISLIKE_COLLECTION_NAME,
                Material.MATERIAL_COMMENT_COLLECTION_NAME);

        // Retrieve course model passed from the previous activity / fragment.
        course = ActivityUtils.getStudyUnit(this, savedInstanceState);

        title.setText(course.getCode());

        /**
         * set listener for upload button when it is clicked
         */
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!uploaded) {
                    // Launch file picker activity to select a PDF file
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    //start an activity result, behaviour defined in onActivityResult method
                    startActivityForResult(intent, REQUEST_PICK_PDF_FILE);
                }


            }
        });

        /**
         * set a listnener for when the submit button is clicked.
         */
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPdfUri != null && !caption.getText().toString().isEmpty()) {
                    // Upload the selected PDF file to Firebase Storage
                    uploadPdfToFirebase(selectedPdfUri);
                } else {
                    Toast.makeText(AddMaterialActivity.this, "Please select a PDF file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * If correct request and result codes and data is not null,
         * change icons of download button to indicate that a file was
         * already uploaded.
         */
        if (requestCode == REQUEST_PICK_PDF_FILE && resultCode == RESULT_OK && notIsDataNull(data)) {
            // Get the selected PDF file URI
            selectedPdfUri = data.getData();
            ImageView uploadIcon = findViewById(R.id.downloadIcon);
            TextView downloadText = findViewById(R.id.downloadText);
            downloadText.setText("Uploaded!");
            uploadIcon.setImageResource(R.drawable.baseline_check_circle_24);
            uploaded = true;
            Toast.makeText(AddMaterialActivity.this, "PDF uploaded successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean notIsDataNull(@Nullable Intent data) {
        return data != null && data.getData() != null;
    }


    /**
     * Helper method to uplaod pdf file to firebase storage
     * @param pdfUri encoded uri of pdf document
     */
    private void uploadPdfToFirebase(Uri pdfUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("materialPdfDocuments")
                .child(course.getId()); // Adjust path as needed

        //get date of upload to make a new material instance
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.GERMANY);
        Date now = new Date();
        String fileName = formatter.format(now);
        StorageReference pdfRef = storageRef.child( fileName + ".pdf");

        UploadTask uploadTask = pdfRef.putFile(pdfUri);
        //Start the upload task
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            /**
             * if upload to storage task successful, upload to firestore.
             * @param taskSnapshot snapshot of task
             */
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // File uploaded successfully
                pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String fileUri = uri.toString();
                    uploadMaterialToDatabase(caption.getText().toString(), fileUri);
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            /**
             * if uplaod unsuccessful, notify user.
             * @param e exception raised.
             */
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle file upload failure
                Toast.makeText(AddMaterialActivity.this, "Failed to upload PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * helper function to upload pdf to firestore.
     * @param caption caption of the material post
     * @param pdfUri uri of the pdf file in storage
     */
    private void uploadMaterialToDatabase(String caption, String pdfUri) {
        String publisherId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Material material = new Material(publisherId, caption, course.getId(), pdfUri);
        materialManager.upload(material, new ItemUploadCallback() {
            @Override
            public void onSuccess() {
                Log.i("Upload material", "Material is uploaded successfully");
                Toast.makeText(AddMaterialActivity.this,
                        "Material is published successfully", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
    }
}
