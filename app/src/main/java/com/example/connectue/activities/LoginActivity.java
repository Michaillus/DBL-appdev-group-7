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

/**
 * The user can login to their account through this activity
 */
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

        /**
         * The user has the option to register a new account:
         *
         * set a click listener for the register button to
         * direct the user to the registration page.
         */
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(goToRegister);
                LoginActivity.this.finish();
            }
        });

        /**
         * If a user already has an account, they can login directly.
         *
         * set a click listener for the login button.
         * Check that all input fields were filled in correctly.
         */
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString().trim();
                String password = passEt.getText().toString().trim();
                 //fields must each be filled in.
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

    /**
     * Helper function to check whether or not the user is verified.
     * @param user the user to check
     * @param callback call back to verification
     * @return a boolean value.
     */
    private Boolean checkUserIsVerified(FirebaseUser user, final VerificationCallback callback) {
        DocumentReference userDoc = db.collection("users").document(user.getUid());
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    /**
                     * if user exists, check verification status.
                     * otherwise log an error.
                     */
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

        /**
         * redirect the user based on verification status.
         */
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

    /**
     * helper function to log user into application.
     * @param email email of user.
     * @param password password of user.
     */
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        //check whether or not user is verified through helper function
                        isVerified = checkUserIsVerified(user, new VerificationCallback() {
                            /**
                             * Callback to check verification. checking verification
                             * is an asynchronous process and requires callback.
                             * @param isVerified the verificaiton status.
                             */
                            @Override
                            public void onVerificationCallback(boolean isVerified) {
                                /**
                                 * if user isn't verified, notify them and end
                                 * the callback function.
                                 */
                                if(!isVerified) {
                                    Toast.makeText(LoginActivity.this,
                                            "Email is not verified", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                /**
                                 *  Sign in success, update UI with the signed-in user's information.
                                 *  Notify the user of login success and redirect
                                 *  them to application home page.
                                 */
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "Login Success",
                                        Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(login);
                                LoginActivity.this.finish();
                            }
                        });

                        /**
                         * If the user entered incorrect login details, notify them
                         */
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