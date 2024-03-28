package com.example.connectue.activities;

import com.example.connectue.databinding.ActivityMajorViewBinding;

public class MajorViewActivity extends StudyUnitViewActivity {

    protected void setBinding() {
        ActivityMajorViewBinding binding = ActivityMajorViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}