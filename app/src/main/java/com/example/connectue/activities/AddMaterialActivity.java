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
import com.example.connectue.interfaces.FireStoreUploadCallback;
import com.example.connectue.managers.MaterialManager;
import com.example.connectue.model.Material;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMaterialActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_PDF_FILE = 1;

    private String courseCode;
    private String courseId;

    private FirebaseFirestore db;
    private Uri selectedPdfUri;

    TextInputEditText caption;
    MaterialManager materialManager;

    Boolean uploaded = false;
    private String TAG = "MatUploadUtil: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_upload);

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

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                courseId= "";
            } else {
                courseId= extras.getString("courseId");
            }
        } else {
            courseId= (String) savedInstanceState.getSerializable("courseId");
        }

        db.collection("courses").document(courseId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null) {
                                courseCode = documentSnapshot.getString("courseCode");
                                title.setText(courseCode);
                            }
                        } else {
                            Toast.makeText(AddMaterialActivity.this, "Failed to retrieve course details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!uploaded) {
                    // Launch file picker activity to select a PDF file
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent, REQUEST_PICK_PDF_FILE);
                }


            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPdfUri != null && !caption.getText().toString().isEmpty()) {
                    Log.d(TAG, "WHATT");
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
        if (requestCode == REQUEST_PICK_PDF_FILE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
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
    }

    private void uploadPdfToFirebase(Uri pdfUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("materialPdfDocuments").child(courseId); // Adjust path as needed

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.GERMANY);
        Date now = new Date();
        String fileName = formatter.format(now);
        StorageReference pdfRef = storageRef.child( fileName + ".pdf");

        UploadTask uploadTask = pdfRef.putFile(pdfUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // File uploaded successfully
                pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String fileUri = uri.toString();
                    uploadMaterialToDatabase(caption.getText().toString(), fileUri);
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle file upload failure
                Toast.makeText(AddMaterialActivity.this, "Failed to upload PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadMaterialToDatabase(String caption, String pdfUri) {
        String publisherId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Material material = new Material(publisherId, caption, courseId, pdfUri);
        materialManager.upload(material, new FireStoreUploadCallback() {
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
