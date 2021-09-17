package com.example.chaudelivery.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.chaudelivery.R;

public class Map_views extends AppCompatActivity {

    String TAG = "Map_views";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_views);
        Log.d(TAG, "onCreate:  "+getIntent().getStringArrayExtra("GEO_POINTS"));
    }
}