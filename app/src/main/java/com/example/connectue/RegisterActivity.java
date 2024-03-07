package com.example.connectue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //views
    EditText emailEt, passEt, fNameEt, lNameEt;
    String selectedProgram;
    Button registerBtn;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerBtn = findViewById(R.id.buttonregister);

        Spinner spinner = (Spinner) findViewById(R.id.ProgramSpinner);
        emailEt = findViewById(R.id.emailET);
        passEt = findViewById(R.id.passwordET);
        fNameEt = findViewById(R.id.FirstNameET);
        lNameEt = findViewById(R.id.LastNameET);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering user...");
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.programs_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString().trim();
                String password = passEt.getText().toString().trim();
                String FirstName = fNameEt.getText().toString().trim();
                String LastName = lNameEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Invalid Email");
                    emailEt.setFocusable(true);
                }
                if (!email.split("@")[1].equalsIgnoreCase("student.tue.nl")) {
                    emailEt.setError("Must use valid TU/e email");
                    emailEt.setFocusable(true);
                }
                if (password.length() < 8) {
                    passEt.setError("Password length must be at least 8 characters long");
                    passEt.setFocusable(true);
                }
                if (TextUtils.isEmpty(FirstName)) {
                    fNameEt.setError("Please enter your First Name");
                    fNameEt.setFocusable(true);
                }
                if (TextUtils.isEmpty(LastName)) {
                    lNameEt.setError("Please enter your First Name");
                    lNameEt.setFocusable(true);
                }
                else {
                    registerUser(email, password, FirstName, LastName, selectedProgram);
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedProgram = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), selectedProgram, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private String TAG = "RegisterUtil: ";

    private void registerUser(String email, String password, String firstName, String lastName, String program) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");

                    // Send verification email
                    sendVerificationEmail(email, password, firstName, lastName, program);
                } else {
                    Log.d(TAG, "createUserWithEmail:failure", task.getException());
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerificationEmail(String email, String password, String firstName, String lastName, String program) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email verification sent.");
                        Toast.makeText(RegisterActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                        addUserToFirestore(user.getUid(), email, password, firstName, lastName, program);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                Intent mInHome = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(mInHome);
                                RegisterActivity.this.finish();
                            }
                        }, 3000);

                        // Wait for the user to click the verification link before adding them to Firestore
//                        waitForVerification(user, email, password, firstName, lastName, program);
                    } else {
                        Log.e(TAG, "Failed to send verification email.", task.getException());
                        Toast.makeText(RegisterActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

//    private void waitForVerification(final FirebaseUser user, String email, String password, String firstName, String lastName, String program) {
//        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    if (user.isEmailVerified()) {
//                        // If email is verified, add user to Firestore
//                        addUserToFirestore(user.getUid(), email, password, firstName, lastName, program);
//                    } else {
//                        // If email is not verified, show a message to the user
//                        Toast.makeText(RegisterActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
//                } else {
//                    Log.e(TAG, "Error reloading user", task.getException());
//                    progressDialog.dismiss();
//                    Toast.makeText(RegisterActivity.this, "Error registering user", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }


    private void addUserToFirestore(String uid, String email, String password, String firstName, String lastName, String program) {
        User user = new StudentUser(uid, email, password, firstName, lastName, program);
        db.collection("users")
                .document("UserTypes")
                .collection("Students")
                .document(uid)
                .set(user)
                .addOnCompleteListener(this,new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User added to Firestore successfully");
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding user to Firestore", e);
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error adding user to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return onSupportNavigateUp();
    }
}