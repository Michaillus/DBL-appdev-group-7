package com.example.connectue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminActivity extends AppCompatActivity {

    private Button backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        backBtn = findViewById(R.id.back_btn);

        initBackButton();
    }

    private void initBackButton() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOtherActivity(MainActivity.class);
            }
        });
    }

    private void toOtherActivity(Class activity) {
        Intent loading = new Intent(AdminActivity.this, activity);
        AdminActivity.this.startActivity(loading);
        AdminActivity.this.finish();
    }

}