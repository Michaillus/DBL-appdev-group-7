package com.example.connectue.utils;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.connectue.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfilePageBasicInfo {
    private final android.content.Context context;
    private final View view;
    private final DocumentSnapshot document;
    private final android.content.res.Resources resources;
    private final FirebaseFirestore db;
    private final FirebaseUser user;
    private Button editBtn;
    private EditText firstName_fld;
    private EditText lastName_fld;
    private TextView majorTextView;
    private EditText email_fld;
    private EditText phone_fld;
    private Spinner majorSpinner;
    private Spinner majorSpinner2;
    private String firstNameStr = "";
    private String lastNameStr = "";
    private String emailStr = "";
    private String phoneStr = "";
    private String spinnerStr = "";
    private String spinnerStr2 = "";
    private boolean isEditing = false;
    private long role = 2;
    private static final String EDITON = "Save";
    private static final String EDITOFF = "Edit";

    public ProfilePageBasicInfo(android.content.Context context, View view,
                                DocumentSnapshot document, android.content.res.Resources resources,
                                FirebaseFirestore db, FirebaseUser user) {
        this.context = context;
        this.view = view;
        this.document = document;
        this.resources = resources;
        this.db = db;
        this.user = user;
        parseDocument();
        if (context != null) {
            initComponents();
        }
    }

    private void parseDocument(){
        if (document == null) {return;}
        firstNameStr = document.getString(General.FIRSTNAME) == null ? "": document.getString(General.FIRSTNAME);
        lastNameStr = document.getString(General.LASTNAME) == null ? "": document.getString(General.LASTNAME);
        emailStr = document.getString(General.EMAIL) == null ? "": document.getString(General.EMAIL);
        phoneStr = document.getString(General.PHONE) == null ? "": document.getString(General.PHONE);
        role = document.getLong(General.ROLE) == null ? 2: document.getLong(General.ROLE);

        if (document.getString(General.PROGRAM) != null) {
            String[] majors = document.getString(General.PROGRAM).split(" ");

            if( majors.length == 0) {
                spinnerStr = " ";
                spinnerStr2 = " ";
            } else if (majors.length == 1) {
                spinnerStr = majors[0];
                spinnerStr2 = " ";
            } else {
                spinnerStr = majors[0];
                spinnerStr2 = majors[1];
            }
        }
    }

    private void initComponents() {
        firstName_fld = view.findViewById(R.id.text_firstName);
        lastName_fld = view.findViewById(R.id.text_lastName);
        email_fld = view.findViewById(R.id.text_email);
        phone_fld = view.findViewById(R.id.text_phone);

        editBtn= view.findViewById(R.id.btn_edit);
        majorSpinner = view.findViewById(R.id.majorSpinner);
        majorSpinner2 = view.findViewById(R.id.majorSpinner2);
        majorTextView = view.findViewById(R.id.major_title);

        majorSpinner.setEnabled(false);
        majorSpinner2.setEnabled(false);

        if (General.isGuest(role)) {
            majorTextView.setVisibility(View.INVISIBLE);
            majorSpinner.setVisibility(View.INVISIBLE);
            majorSpinner2.setVisibility(View.INVISIBLE);
        }

        initSpinner();
        initTextSection();
        initEditButton();
    }

    /**
     * Initialize 2 drop down menus for 2 majors.
     */
    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.programs_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        majorSpinner.setAdapter(adapter);
        majorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int deviceScreenSize = resources.getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK;

                TextView textView = (TextView) parent.getChildAt(0);
                if (deviceScreenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                        deviceScreenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                    // For tablets, set a larger font size
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                } else {
                    // For phones, set a smaller font size
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
                textView.setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        majorSpinner2.setAdapter(adapter);
        majorSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int deviceScreenSize = resources.getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK;

                TextView textView = (TextView) parent.getChildAt(0);
                if (deviceScreenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                        deviceScreenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                    // For tablets, set a larger font size
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                } else {
                    // For phones, set a smaller font size
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
                textView.setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Initialize all textfieds in the profile page, like first name, the second name, phone number
     *  , and majors. Also, set all textfields uneditable.
     */
    private void initTextSection() {
        setTestFields();
        switchTextFields(isEditing);
    }

    private void setTestFields() {
        firstName_fld.setText(firstNameStr);
        lastName_fld.setText(lastNameStr);
        email_fld.setText(emailStr);
        phone_fld.setText(phoneStr);

        String[] items = resources.getStringArray(R.array.programs_array);
        setSpinner(items, spinnerStr, majorSpinner);
        setSpinner(items, spinnerStr2, majorSpinner2);
    }

    private void setSpinner(String[] items, String spinnerString, Spinner spinner) {
        int position = -1;
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(spinnerString)) {
                position = i;
                break;
            }
        }

        if (position != -1) {
            spinner.setSelection(position);
        }
    }

    private void switchTextFields(boolean onOff) {
        firstName_fld.setEnabled(onOff);
        lastName_fld.setEnabled(onOff);
        email_fld.setEnabled(onOff);
        phone_fld.setEnabled(onOff);
    }

    private void initEditButton() {
        editBtn.setText(EDITOFF);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing) {
                    editBtn.setText(EDITOFF);
                    updateInfo();
                } else {
                    editBtn.setText(EDITON);
                }

                isEditing = !isEditing;
                switchTextFields(isEditing);
                setTestFields();

                majorSpinner.setEnabled(!majorSpinner.isEnabled());
                majorSpinner2.setEnabled(!majorSpinner2.isEnabled());
            }
        });
    }

    private void updateInfo() {
        Map<String, Object> uploadInfo = new HashMap<>();
        firstNameStr = firstName_fld.getText().toString().trim();
        lastNameStr = lastName_fld.getText().toString().trim();
        emailStr = email_fld.getText().toString().trim();
        phoneStr = phone_fld.getText().toString().trim();
        spinnerStr = (String) majorSpinner.getSelectedItem();
        spinnerStr2 = (String) majorSpinner2.getSelectedItem();

        uploadInfo.put(General.FIRSTNAME, firstNameStr);
        uploadInfo.put(General.LASTNAME, lastNameStr);
        uploadInfo.put(General.PROGRAM, spinnerStr + " " + spinnerStr2);
        uploadInfo.put(General.EMAIL, emailStr);
        uploadInfo.put(General.PHONE, phoneStr);

        db.collection(General.USERCOLLECTION).document(user.getUid()).update(uploadInfo);
    }
}
