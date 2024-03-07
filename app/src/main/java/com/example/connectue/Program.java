package com.example.connectue;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Program {
    public List<Course> coursesInProgram;
    private FirebaseFirestore db;
    private String TAG = "ProgramsUtil: ";

    public Program(String programID) {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("programs").document(programID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the value of the field
                        Object value = document.get("programCourses");
                        if (value != null) {
                            // Do something with the value
                            Log.d(TAG, "Value of programCourses: " + value.toString());
                        } else {
                            Log.d(TAG, "Field 'programCourses' does not exist or is null.");
                        }
                    } else {
                        Log.d(TAG, "Document does not exist.");
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }
}
