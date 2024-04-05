package com.example.connectue.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.connectue.fragments.ManageReportHistoryFragment;
import com.example.connectue.R;
import com.example.connectue.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This class represents the AdminActivity for admin users.
 */
public class AdminActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private ImageButton backBtn;
    private BottomNavigationView naviBar;

    /**
     * Initializes the activity when it is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_admin);
        replaceFragment(new ManageReportHistoryFragment());

        // Initialize back button and navigation bar
        initBackButton();
        initNaviBar();
    }

    /**
     * Initialize back button.
     */
    private void initBackButton() {
        backBtn = findViewById(R.id.admin_back_btn);

        // When clicking the back button, start a new main activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * Start a new main activity.
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                toOtherActivity(MainActivity.class);
            }
        });
    }

    /**
     * Initialize navigation bar.
     */
    private void initNaviBar() {
        naviBar = findViewById(R.id.buttom_bar_admin_nav);

        // Set listener for item selection in the navigation bar
        naviBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            // Check if the selected item is for managing reported history
            if (itemId == R.id.post_fragment) {
                // Replace the current fragment with the ManageReportHistoryFragment
                replaceFragment(new ManageReportHistoryFragment());
            }
                    return true;
        });

        // Set the navigation bar invisible
        naviBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Start a new main activity.
     *
     * @param activity The class of the activity to start.
     */
    private void toOtherActivity(Class activity) {
        // Create an intent to start the specified activity
        Intent loading = new Intent(AdminActivity.this, activity);
        // Start the new activity
        AdminActivity.this.startActivity(loading);
        // Finish the current AdminActivity
        AdminActivity.this.finish();
    }

    /**
     * Replaces the current fragment with the given fragment.
     *
     * @param fragment The fragment to replace the current one with.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragment, fragment);
        // Commit the transaction to apply the changes
        fragmentTransaction.commit();
    }

    /**
     * Hide the navigation bar.
     */
    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}