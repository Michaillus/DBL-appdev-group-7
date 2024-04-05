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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the basic information section of the user's profile page.
 * It manages the UI components and functionality related to displaying and editing
 * user information such as name, email, phone number, and majors based on the role of the user.
 */
public class ProfilePageBasicInfo {
    // Declare variables
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

    /**
     * Constructs a new ProfilePageBasicInfo object with the given context, view,
     * document snapshot, resources, Firestore instance, and Firebase user.
     *
     * @param context   The context in which the ProfilePageBasicInfo is operating
     * @param view      The view associated with the ProfilePageBasicInfo
     * @param document  The document snapshot containing user information
     * @param resources The resources object for accessing application resources
     * @param db        The Firestore instance
     * @param user      The Firebase user object representing the current user
     */
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

    //get the context
    public android.content.Context getContext() {return context;}
//    get the view
    public View getView() {return view;}
//    get the documentSnapshot
    public DocumentSnapshot getDocument() {return document;}
//    get the resources
    public android.content.res.Resources getResources() {return resources;}
//    get the firebase database
    public FirebaseFirestore getDataBase() {return db;}
//    get the user reference
    public FirebaseUser getUser() {return user;}

    /**
     * Parses the document snapshot to extract user information.
     */
    private void parseDocument(){
        // Get user name, email, phone and role information from document
        if (document == null) {return;}
        firstNameStr = document.getString(General.FIRSTNAME) == null ? "": document.getString(General.FIRSTNAME);
        lastNameStr = document.getString(General.LASTNAME) == null ? "": document.getString(General.LASTNAME);
        emailStr = document.getString(General.EMAIL) == null ? "": document.getString(General.EMAIL);
        phoneStr = document.getString(General.PHONE) == null ? "": document.getString(General.PHONE);
        role = document.getLong(General.ROLE) == null ? 2: document.getLong(General.ROLE);

        // Parse and set majors information
        if (document.getString(General.PROGRAM) != null) {
            // Split the major string in firebase using space as the separator
            String[] majors = document.getString(General.PROGRAM).split(" ");

            // If the major string is empty, then it means the user does not set majors
            if( majors.length == 0) {
                spinnerStr = " ";
                spinnerStr2 = " ";
            } else if (majors.length == 1) { // If there is one major in the major string array
                spinnerStr = majors[0];
                spinnerStr2 = " ";
            } else { // If there are two majors in the major string array
                spinnerStr = majors[0];
                spinnerStr2 = majors[1];
            }
        }
    }

    /**
     * Initialize UI components on profile page.
     */
    private void initComponents() {
        // Initialize UI components
        firstName_fld = view.findViewById(R.id.text_firstName);
        lastName_fld = view.findViewById(R.id.text_lastName);
        email_fld = view.findViewById(R.id.text_email);
        phone_fld = view.findViewById(R.id.text_phone);

        editBtn= view.findViewById(R.id.btn_edit);
        majorSpinner = view.findViewById(R.id.majorSpinner);
        majorSpinner2 = view.findViewById(R.id.majorSpinner2);
        majorTextView = view.findViewById(R.id.major_title);

        // Set the initial visibility of two spinners
        majorSpinner.setEnabled(false);
        majorSpinner2.setEnabled(false);

        // If the user is guest, set major space invisible to the user
        if (General.isGuest(role)) {
            majorTextView.setVisibility(View.INVISIBLE);
            majorSpinner.setVisibility(View.INVISIBLE);
            majorSpinner2.setVisibility(View.INVISIBLE);
        }

        // Initialize components
        initSpinner();
        initTextSection();
        initEditButton();
    }

    /**
     * Initialize 2 drop down menus for 2 majors.
     */
    private void initSpinner() {
        // Set up adapter for spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.programs_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter for two major spinners
        setSpinnerAdapter(adapter, majorSpinner);
        setSpinnerAdapter(adapter, majorSpinner2);
    }

    /**
     * Set adapter for major spinner.
     *
     * @param adapter The adapter for spinner
     * @param spinner The input major spinner
     */
    private void setSpinnerAdapter(ArrayAdapter<CharSequence> adapter, Spinner spinner) {
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * When clicking one item on the drop-down menu, the text displayed in the spinner
             * will change.
             *
             * @param parent The AdapterView where the selection happened
             * @param view The view within the AdapterView that was clicked
             * @param position The position of the view in the adapter
             * @param id The row id of the item that is selected
             */
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
                // Set the color of the text in the spinner
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

    /**
     * Set initial text for each fields.
     */
    private void setTestFields() {
        // Set text for name, email, phone field on the profile page
        firstName_fld.setText(firstNameStr);
        lastName_fld.setText(lastNameStr);
        email_fld.setText(emailStr);
        phone_fld.setText(phoneStr);

        // Get all majors string from resource
        String[] items = resources.getStringArray(R.array.programs_array);
        setSpinner(items, spinnerStr, majorSpinner);
        setSpinner(items, spinnerStr2, majorSpinner2);
    }

    /**
     * Set major in the major spinner.
     *
     * @param items         The all majors string get from resource
     * @param spinnerString The selected major string
     * @param spinner       The major spinner
     */
    private void setSpinner(String[] items, String spinnerString, Spinner spinner) {
        int position = -1;
        for (int i = 0; i < items.length; i++) {
            // Find the position of the major string through all major string items
            if (items[i].equals(spinnerString)) {
                position = i;
                break;
            }
        }

        if (position != -1) {
            // Set the spinner using the major string in the position
            spinner.setSelection(position);
        }
    }

    /**
     * Set the visibility of UI components.
     * @param onOff The boolean value about if the component can be set visible
     */
    private void switchTextFields(boolean onOff) {
        firstName_fld.setEnabled(onOff);
        lastName_fld.setEnabled(onOff);
        email_fld.setEnabled(onOff);
        phone_fld.setEnabled(onOff);
    }

    /**
     * Initialize Edit button.
     */
    private void initEditButton() {
        editBtn.setText(EDITOFF);
        // Set the click listener for the edit button
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle between "Edit" and "Save" modes
                if (isEditing) {
                    editBtn.setText(EDITOFF);
                    updateInfo(); // Save changes if in "Save" mode
                } else {
                    editBtn.setText(EDITON);
                }

                isEditing = !isEditing;
                switchTextFields(isEditing);
                setTestFields();

                // When clicking edit button, set major spinners enable to edit
                majorSpinner.setEnabled(!majorSpinner.isEnabled());
                majorSpinner2.setEnabled(!majorSpinner2.isEnabled());
            }
        });
    }

    /**
     * Updates the user information in the Firestore database with the edited values.
     * This method retrieves the edited values from the UI components, constructs a map
     * containing the updated information, and then updates the Firestore document.
     */
    private void updateInfo() {
        // Initialize map to store updated information
        Map<String, Object> uploadInfo = new HashMap<>();

        // Retrieve and trim edited values from UI components
        firstNameStr = firstName_fld.getText().toString().trim();
        lastNameStr = lastName_fld.getText().toString().trim();
        emailStr = email_fld.getText().toString().trim();
        phoneStr = phone_fld.getText().toString().trim();
        spinnerStr = (String) majorSpinner.getSelectedItem();
        spinnerStr2 = (String) majorSpinner2.getSelectedItem();

        // Add updated values to the map
        uploadInfo.put(General.FIRSTNAME, firstNameStr);
        uploadInfo.put(General.LASTNAME, lastNameStr);
        uploadInfo.put(General.PROGRAM, spinnerStr + " " + spinnerStr2);
        uploadInfo.put(General.EMAIL, emailStr);
        uploadInfo.put(General.PHONE, phoneStr);

        // Update Firestore document with the new information
        db.collection(General.USERCOLLECTION).document(user.getUid()).update(uploadInfo);
    }
}
