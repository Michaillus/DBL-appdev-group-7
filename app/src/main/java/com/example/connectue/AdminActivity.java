package com.example.connectue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.connectue.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private ImageButton backBtn;
    private BottomNavigationView naviBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_admin);
        replaceFragment(new ManageReportHistoryFragment());

        initBackButton();
        initNaviBar();
    }

    private void initBackButton() {
        backBtn = findViewById(R.id.admin_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOtherActivity(MainActivity.class);
            }
        });
    }
    private void initNaviBar() {
        naviBar = findViewById(R.id.buttom_bar_admin_nav);
        naviBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.post_fragment) {
                replaceFragment(new ManageReportHistoryFragment());
            }
                    return true;
        });
    }

    private void toOtherActivity(Class activity) {
        Intent loading = new Intent(AdminActivity.this, activity);
        AdminActivity.this.startActivity(loading);
        AdminActivity.this.finish();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragment, fragment);
        fragmentTransaction.commit();
    }
}