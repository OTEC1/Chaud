package com.example.chaudelivery.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.chaudelivery.R;
import com.example.chaudelivery.utils.utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sp;


    @Override
    protected void onResume() {
        super.onResume();
        new utils().openFragment(new notification(),"notification",1,this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNav);
        new utils().bottom_nav(bottomNavigationView,this,sp);
    }
}