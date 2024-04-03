package com.example.connectue.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.connectue.fragments.ChannelsFragment;
import com.example.connectue.fragments.HomeFragment;
import com.example.connectue.fragments.ProfileFragment;
import com.example.connectue.R;
import com.example.connectue.databinding.ActivityMainBinding;

/**
 * This activity holds the fragments for the
 * main pages of the application.
 */
public class MainActivity extends AppCompatActivity {

    //set attribute for binding of the activity
    ActivityMainBinding binding;
    private String pageIntent = "home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment(getSupportFragmentManager()));

        /**
         * get the intent of the fragment
         * on which we want to open the main activity
         */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                pageIntent= "home";
            } else {
                pageIntent= extras.getString("pageIntent");
            }
        } else {
            pageIntent= (String) savedInstanceState.getSerializable("pageIntent");
        }

        //Default fragment is home page.
        if (pageIntent.equals("channels")) {
            replaceFragment(new ChannelsFragment());
        } else {
            replaceFragment(new HomeFragment(getSupportFragmentManager()));
        }


        /**
         * Get selected item data from bottom nav bar
         * to navigate between fragments .
         */
        binding.bottomNavigationView3.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment(getSupportFragmentManager()));
            } else if (itemId == R.id.channels) {
                replaceFragment(new ChannelsFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }

            return true;
        });


    }

    /**
     * Helper method to replace fragment visible in main activity
     * @param fragment the fragment to replace with
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}