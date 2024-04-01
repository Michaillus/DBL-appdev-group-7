package com.example.connectue.activities;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.connectue.R;
import com.example.connectue.interfaces.VerificationCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sh;
    EditText emailEt, passEt;
    //views
    Button mRegisterBtn, mLoginBtn;
    private boolean isVerified;

    /**
     * Instance of a FireStore authentication database.
     */
    private FirebaseAuth mAuth;

    /**
     * Instance of a FireBase FireStore database.
     */
    private FirebaseFirestore db;

    /**
     * Class tag for logs.
     */
    private static final String TAG = "LoginActivity";

    private Boolean verifiedObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Instantiate instance of SharedPreferences
        sh = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        mRegisterBtn = findViewById(R.id.buttonregister);
        mLoginBtn = findViewById(R.id.buttonlgn);

        emailEt = findViewById(R.id.emailETL);
        passEt = findViewById(R.id.passwordETL);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();




        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(goToRegister);
                LoginActivity.this.finish();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString().trim();
                String password = passEt.getText().toString().trim();
                if (TextUtils.isEmpty(emailEt.getText().toString().trim())) {
                    emailEt.setError("Must fill in all fields");
                    emailEt.setFocusable(true);
                    return;
                } else if (TextUtils.isEmpty(passEt.getText().toString().trim())) {
                    passEt.setError("Must fill in all fields");
                    passEt.setFocusable(true);
                    return;
                }
                loginUser(email, password);

            }
        });
    }

    private Boolean checkUserIsVerified(FirebaseUser user, final VerificationCallback callback) {
        DocumentReference userDoc = db.collection("users").document(user.getUid());
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the value of isVerified
                        verifiedObject = document.getBoolean("isVerified");
                        if (verifiedObject != null) {
                            callback.onVerificationCallback(verifiedObject);
                            Log.i(TAG, "Value of field 'isVerified': " + verifiedObject.toString());
                        } else {
                            callback.onVerificationCallback(false);
                            Log.i(TAG, "Field 'isVerified' is null.");
                        }
                    } else {
                        callback.onVerificationCallback(false);
                        Log.e(TAG, "No such document");
                    }
                } else {
                    callback.onVerificationCallback(false);
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });
        if (verifiedObject == null) {
            Log.e(TAG, "Field 'isVerified' is null.");
            return false;
        } else if (verifiedObject.equals(true)) {
            return true;
        } else if (user.isEmailVerified()) {
            db.collection("users").document(user.getUid()).update("isVerified", true);
            return true;
        } else {
            return false;
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        isVerified = checkUserIsVerified(user, new VerificationCallback() {
                            @Override
                            public void onVerificationCallback(boolean isVerified) {
                                if(!isVerified) {
                                    Toast.makeText(LoginActivity.this,
                                            "Email is not verified", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "Login Success",
                                        Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(login);
                                LoginActivity.this.finish();
                            }
                        });

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Incorrect credentials. Try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }



}