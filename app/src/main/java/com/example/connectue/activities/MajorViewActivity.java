package com.example.connectue.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.connectue.R;
import com.example.connectue.databinding.ActivityCourseViewBinding;
import com.example.connectue.databinding.ActivityMajorViewBinding;
import com.example.connectue.fragments.MaterialsFragment;
import com.example.connectue.fragments.QuestionsFragment;
import com.example.connectue.fragments.ReviewsFragment;
import com.example.connectue.model.Course;
import com.example.connectue.utils.ActivityUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MajorViewActivity extends StudyUnitViewActivity {

    protected void setBinding() {
        ActivityMajorViewBinding binding = ActivityMajorViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}