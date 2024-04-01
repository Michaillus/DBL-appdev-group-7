package com.example.connectue.activities;

import com.example.connectue.databinding.ActivityMajorViewBinding;

/**
 * Activity that shows a major and it's reviews.
 */
public class MajorViewActivity extends StudyUnitViewActivity {

    protected void setBinding() {
        ActivityMajorViewBinding binding = ActivityMajorViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}