package com.example.connectue.activities;

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

import com.example.connectue.R;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The activity in which a user can register for a new account.
 */
public class RegisterActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "RegisterActivity";

    //views
    EditText emailEt, passEt, fNameEt, lNameEt;
    String selectedProgram;
    Button registerBtn;
    ProgressDialog progressDialog;

    FloatingActionButton backBtn;
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
        backBtn = findViewById(R.id.back_btn);

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
                int role = 0;
                //Check registration requirements
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Must use valid TU/e email or other (for guest)");
                    emailEt.setFocusable(true);
                    return;
                }
                if (password.length() < 8) {
                    passEt.setError("Password length must be at least 8 characters long");
                    passEt.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(FirstName)) {
                    fNameEt.setError("Please enter your First Name");
                    fNameEt.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(LastName)) {
                    lNameEt.setError("Please enter your First Name");
                    lNameEt.setFocusable(true);
                    return;
                }
                //Determine role of user: 1 = student, 2 = guest
                if (email.split("@")[1].equals("student.tue.nl")) {
                    role = 1;
                } else if (!email.split("@")[1].equals("student.tue.nl")) {
                    role = 2;
                }

                registerUser(email, password, FirstName, LastName, selectedProgram, role);


            }
        });

        backBtn.setOnClickListener(v -> {
           Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
           startActivity(intent);
           RegisterActivity.this.finish();
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedProgram = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Helper method to register a user.
     * @param email the email of the user
     * @param password the password the user chose
     * @param firstName the user's name
     * @param lastName the user's last name
     * @param program the academic program the user chose (if applicable)
     * @param role the role of the user (guest, user, admin)
     */
    private void registerUser(String email, String password, String firstName, String lastName,
                              String program, int role) {
        progressDialog.show();
        //create an auth instance of of user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    /**
                     * when addition to auth queue completed,
                     * send verification email
                     * @param task authResult task
                     */
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if auth is successful, send verification email
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");

                    // Send verification email
                    sendVerificationEmail(email, password, firstName, lastName, program, role);
                    //otherwise notify user of error
                } else {
                    Log.d(TAG, "createUserWithEmail:failure", task.getException());
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,
                            "Registration failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * helper method to send verification email to user.
     * @param email the email of the user
     * @param password the password the user chose
     * @param firstName the user's name
     * @param lastName the user's last name
     * @param program the academic program the user chose (if applicable)
     * @param role the role of the user (guest, user, admin)
     */
    private void sendVerificationEmail(String email, String password, String firstName, String lastName, String program, int role) {
        FirebaseUser user = mAuth.getCurrentUser();
        //if user of mAuth isn't null, send a verification email.
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //if email send is sent successfully, add user to database
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email verification sent.");
                        Toast.makeText(RegisterActivity.this,
                                "Verification email sent",
                                Toast.LENGTH_SHORT).show();
                        //add user to database
                        addUserToFirestore(user.getUid(), email, firstName, lastName, program, role);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                Intent mInHome = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(mInHome);
                                RegisterActivity.this.finish();
                            }
                        }, 3000);

                        //Otherwise notify user of error
                    } else {
                        Log.e(TAG, "Failed to send verification email.", task.getException());
                        Toast.makeText(RegisterActivity.this,
                                "Failed to send verification email",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    /**
     * helper method to add user to database
     * @param uid id of user
     * @param email email of user
     * @param firstName name of user
     * @param lastName last name of user
     * @param program program of user
     * @param role role of user.
     */
    private void addUserToFirestore(String uid, String email, String firstName, String lastName,
                                    String program, int role) {
        User user = new User(uid, firstName, lastName, false,
                email, null, null, program, role);
        UserManager userManager = new UserManager(FirebaseFirestore.getInstance(),
                User.USER_COLLECTION_NAME);
        //add user to database.
        userManager.set(user, uid, new ItemUploadCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "User added to Firestore successfully");
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,
                        "Registration successful",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error adding user to Firestore", e);
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,
                        "Error during the registration occurred",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return onSupportNavigateUp();
    }
}