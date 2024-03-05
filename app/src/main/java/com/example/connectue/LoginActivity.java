package com.example.connectue;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sh;
    EditText emailEt, passEt;
    //views
    Button mRegisterBtn, mLoginBtn;
    private boolean isVerified;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private FirebaseFirestore db;

    private String TAG = "Login user: ";
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
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, user.getEmail());

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

                loginUser(email, password);

            }
        });
    }

    private Boolean checkUserIsVerified() {
        if (user.isEmailVerified()) {
            db.collection("users").document(user.getUid()).update("isVerified", true);
            return true;
        }
        Toast.makeText(LoginActivity.this, "User not verified",
                Toast.LENGTH_SHORT).show();
        return false;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkUserIsVerified();
                        isVerified = checkUserIsVerified();
                        if(!isVerified) {
                            return;
                        }
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Login Success",
                                Toast.LENGTH_SHORT).show();
                        Intent login = new Intent(LoginActivity.this, HomePage.class);
                        LoginActivity.this.startActivity(login);
                        LoginActivity.this.finish();
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