package com.example.chaudelivery.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.chaudelivery.R;
import com.example.chaudelivery.UI.Login;
import com.example.chaudelivery.UI.MainActivity;
import com.example.chaudelivery.UI.account;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class utils {


    private SharedPreferences sp;

    //Cache on  temp data on User machine
    public SharedPreferences init(Context view) {
        return sp = Objects.requireNonNull(view.getSharedPreferences(view.getString(R.string.app_name), Context.MODE_PRIVATE));
    }

    public SharedPreferences start(Context context) {
        return sp = PreferenceManager.getDefaultSharedPreferences(context);
    }


    //Bottom  Nav
    public void bottom_nav(BottomNavigationView bottomNav, AppCompatActivity appCompatActivity, SharedPreferences sp) {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.notification:
                    appCompatActivity.startActivity(new Intent(appCompatActivity, MainActivity.class));
                    return true;

                case R.id.account:
                    openFragment(new account(), "account", 1, appCompatActivity);
                    return true;


                case R.id.log:
                    buildAlertMessageNoGps(appCompatActivity);
                    return true;

            }
            return false;
        });
    }


    private void buildAlertMessageNoGps(Context context) {
        if (FirebaseAuth.getInstance().getUid() != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.app_name))
                    .setMessage("Pls Sign out , would you like to Sign out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            init(context).edit().putString(context.getString(R.string.LAST_SIGN_IN_DELIVERY), FirebaseAuth.getInstance().getUid()).apply();
                            FirebaseAuth.getInstance().signOut();
                            context.startActivity(new Intent(context, Login.class));

                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else
            context.startActivity(new Intent(context, Login.class));
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
        return null;
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


    public boolean verify(EditText name, EditText email, EditText phone, EditText pass, EditText confirm_pass, EditText delivery_D, Context context) {

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
        } else if (delivery_D.getText().toString().isEmpty()) {
            new utils().check_edit_text(delivery_D, "Pls fill out field");
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


    //Delivery Guy details
    public void CACHE_VENDOR(UserLocation user, Context context, int i, String tag) {
        if (i == 0)
            init(context).edit().putString(tag, null).apply();
        SharedPreferences.Editor collection = init(context).edit();
        String area = new Gson().toJson(user);
        collection.putString(tag, area);
        collection.apply();
        context.startActivity(new Intent(context, MainActivity.class));

    }


    //Get Sign in Vendor.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public UserLocation GET_DELIVERY_CACHED(Context view, String tag) {
        String arrayListString = init(view).getString(tag, null);
        Type type = new TypeToken<UserLocation>() {
        }.getType();
        return new Gson().fromJson(arrayListString, type);

    }


    public void USER_DATA(Context context) {

        DocumentReference reference = FirebaseFirestore.getInstance().collection(context.getString(R.string.DELIVERY_LOCATION)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        reference.get().addOnCompleteListener(o -> {
            if (o.isSuccessful())
                CACHE_VENDOR(o.getResult().toObject(UserLocation.class), context, 0, context.getString(R.string.VENDOR));
            else {
                new utils().message("Error Occurred" + o.getException(), context);
                USER_DATA(context);
            }
        });
    }


    //IMG LOAD PROGRESS
    public void IMG(CircleImageView poster_value, String url, ProgressBar progressBar) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(poster_value.getContext()).load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .apply(requestOptions).into(poster_value);
    }

    public Map<String, Object> maps(String delivery_uid, int x) {
        Map<String, Object> p = new HashMap<>();
        if (x == 0)
            p.put("Delivery", delivery_uid);
        if (x == 1)
            p.put("DStatus", true);

        return p;
    }
}
