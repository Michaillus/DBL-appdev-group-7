package com.example.connectue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView addPost = findViewById(R.id.addPost);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddPost = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(intentAddPost);
            }
        });

        NavigationBar.addNavigationBarActivitySwitch(this);
    }
}