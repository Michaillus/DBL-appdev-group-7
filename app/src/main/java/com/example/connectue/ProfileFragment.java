package com.example.connectue;

import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = "Profile";

    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentSnapshot document;
    private String firstNameStr = "";
    private String lastNameStr = "";
    private String emailStr = "";
    private String phoneStr = "";
    private String majorStr = "";
    private boolean isEditing;
    private boolean isAdmin;
    Button editBtn;
    Button logoutBtn;
    Button deleteBtn;
    Button postHisBtn;
    Button reviewHisBtn;
    Button adminBtn;
    EditText firstName_fld;
    EditText lastName_fld;
    EditText major_fld;
    EditText email_fld;
    EditText phone_fld;


    public ProfileFragment() {
        // Required empty public constructor
        isEditing = false;
        isAdmin = false;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.i(TAG, "start executing onCreate");
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG, user.getUid());
        DocumentReference documentReference = db.collection(General.USERCOLLECTION).document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();

                    if (document.exists()) {
                        Log.i(TAG,"document fetched");
                        Log.i(TAG, document.toString());
                        parseDocument();
                        updateUIComponents();
                    } else {
                        Log.d(TAG, "No such user document");
                    }
                } else {
                    Log.d(TAG, "get task failed with ", task.getException());
                }
            }
        }
        );
        Log.i(TAG, "end executing onCreate, first name: " + firstNameStr);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.i(TAG, "start executing crete view");
        Log.i(TAG, "before: " + firstNameStr);
        initComponents(view);
        return view;
    }

    private void parseDocument() {
        if (document.get(General.FIRSTNAME) != null) {
            Log.i(TAG, "document-first name not null");
            Log.i(TAG, "before: " + firstNameStr);
            firstNameStr = (String) document.get(General.FIRSTNAME);
            Log.i(TAG,"after: " + firstNameStr);
        }
        if (document.get(General.LASTNAME) != null) {  lastNameStr = (String) document.get(General.LASTNAME);}
        if (document.get(General.EMAIL) != null) { emailStr = (String) document.get(General.EMAIL);}
        if (document.get(General.PROGRAM) != null) { majorStr = (String) document.get(General.PROGRAM);}
        if (document.get(General.PHONE) != null) { phoneStr = (String) document.get(General.PHONE);}
    }

    private void initComponents(View view) {
        firstName_fld = view.findViewById(R.id.text_firstName);
        lastName_fld = view.findViewById(R.id.text_lastName);
        major_fld = view.findViewById(R.id.text_major);
        email_fld = view.findViewById(R.id.text_email);
        phone_fld = view.findViewById(R.id.text_phone);

        editBtn= view.findViewById(R.id.btn_edit);
        logoutBtn = view.findViewById(R.id.btn_logout);
        deleteBtn = view.findViewById(R.id.btn_deleteAccount);
        postHisBtn = view.findViewById(R.id.btn_postHistory);
        reviewHisBtn  = view.findViewById(R.id.btn_reviewHistory);
        adminBtn  = view.findViewById(R.id.btn_admmin);

        initTextSection(view);
    }

    private void initTextSection(View view) {
        setTest();

        firstName_fld.setEnabled(false);
        lastName_fld.setEnabled(false);
        major_fld.setEnabled(false);
        email_fld.setEnabled(false);
        phone_fld.setEnabled(false);
    }

    private void setTest() {
        firstName_fld.setText(firstNameStr);
        lastName_fld.setText(lastNameStr);
        major_fld.setText(majorStr);
        email_fld.setText(emailStr);
        phone_fld.setText(phoneStr);
    }

    private void updateUIComponents() {
        if (getContext() != null) {
            setTest();
        }
    }
}