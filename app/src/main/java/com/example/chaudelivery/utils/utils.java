package com.example.chaudelivery.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chaudelivery.R;
import com.example.chaudelivery.UI.Login;
import com.example.chaudelivery.UI.MainActivity;
import com.example.chaudelivery.UI.account;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class utils {






    //Bottom  Nav
    public void bottom_nav(BottomNavigationView bottomNav, AppCompatActivity appCompatActivity, SharedPreferences sp) {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){

                case  R.id.notification:
                    appCompatActivity.startActivity(new Intent(appCompatActivity, MainActivity.class));
                    return  true;

                case  R.id.account:
                    openFragment(new account(),"account",1,appCompatActivity);
                    return  true;


                case  R.id.log:
                    appCompatActivity.startActivity(new Intent(appCompatActivity, Login.class));
                    return  true;

            }
            return false;
        });
    }


    //Fragment Open from Activity
    public void openFragment(androidx.fragment.app.Fragment fragment, String my_fragment, int a, AppCompatActivity context) {
        FragmentTransaction fragmentTransaction = context.getSupportFragmentManager().beginTransaction();
        reuse_fragment(fragmentTransaction, fragment, my_fragment, BUNDLE(new Bundle(), a), R.id.frameLayout);
    }



    //Reuse component
    private void reuse_fragment(FragmentTransaction fragmentTransaction, Fragment fragment, String my_fragment, Bundle b, int frameLayout) {
        fragment.setArguments(b);
        fragmentTransaction.replace(frameLayout, fragment, my_fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private Bundle BUNDLE(Bundle bundle, int a) {
        return  null;
    }


}
