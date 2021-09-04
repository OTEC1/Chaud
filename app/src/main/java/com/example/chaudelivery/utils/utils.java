package com.example.chaudelivery.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class utils {


    private  SharedPreferences sp;

    //Cache on  temp data on User machine
    public SharedPreferences instantiate_shared_preferences( Context view) {
        return sp = Objects.requireNonNull(view.getSharedPreferences(view.getString(R.string.app_name), Context.MODE_PRIVATE));
    }



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



    //to String
    public String Stringify(Object obj) {
        return String.valueOf(obj);
    }


    //String .replace
    public String clean(String v, String change) {
        return v.replace(change, "");
    }

    //Toast
    public void message(String m, Context view) {
        Toast.makeText(view, m, Toast.LENGTH_SHORT).show();
    }

    //SnackBar
    public void message2(String s, Activity v) {
        View parentLayout = v.findViewById(android.R.id.content);
        Snackbar.make(parentLayout, s, Snackbar.LENGTH_LONG).show();

    }



    public boolean verify(EditText  name,EditText email,EditText phone,EditText pass,EditText confirm_pass,Context context) {

        if (name.getText().toString().isEmpty()) {
            new utils().check_edit_text(name, "Pls fill out field");
            return false;
        } else if (email.getText().toString().isEmpty()) {
            new utils().check_edit_text(email, "Pls fill out field");
            return false;
        } else if (phone.getText().toString().isEmpty()) {
            new utils().check_edit_text(phone, "Pls fill out field");
            return false;
        } else if (pass.getText().toString().isEmpty()) {
            new utils().check_edit_text(pass, "Pls fill out field");
            return false;
        } else if (confirm_pass.getText().toString().isEmpty()) {
            new utils().check_edit_text(confirm_pass, "Pls fill out field");
            return false;
        } else if (!doStringsMatch(pass.getText().toString(), confirm_pass.getText().toString())) {
            new utils().message("Password does not match", context);
            return false;
        } else
            return true;
    }


    public void check_edit_text(EditText edit, String string) {
        if (edit.getText().toString().isEmpty()) {
            edit.setError(string);
            edit.requestFocus();
        }
    }


    public static boolean doStringsMatch(String s1, String s2) {
        return s1.equals(s2);
    }

}
