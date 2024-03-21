package com.example.connectue.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Program {
    public String programName;

    public Program(String programName) {
        this.programName = programName;
    }

    public String getProgramName() {
        return programName;
    }
}
