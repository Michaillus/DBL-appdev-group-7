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

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private String pageIntent = "home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment(getSupportFragmentManager()));

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

        if (pageIntent.equals("channels")) {
            replaceFragment(new ChannelsFragment());
        } else {
            replaceFragment(new HomeFragment(getSupportFragmentManager()));
        }


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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}