package com.example.chaudelivery.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.example.chaudelivery.model.User;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class utils {


    private SharedPreferences sp;
    private Bitmap bitmap1;

    //Cache on temp data on User machine
    public SharedPreferences init(Context view) {
        return sp = Objects.requireNonNull(view.getSharedPreferences(view.getString(R.string.app_name), Context.MODE_PRIVATE));
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
                    buildSignOutalert(appCompatActivity);
                    return true;

            }
            return false;
        });
    }


    private void buildSignOutalert(Context context) {
        if (FirebaseAuth.getInstance().getUid() != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.app_name))
                    .setMessage("Would you like to Sign out ?")
                    .setCancelable(false)
                    .setNegativeButton("No", (w, b) -> {
                        w.dismiss();
                    })
                    .setPositiveButton("Yes", (dialog, id) -> {
                        init(context).edit().putString(context.getString(R.string.LAST_SIGN_IN_DELIVERY), FirebaseAuth.getInstance().getUid()).apply();
                        init(context).edit().putString(context.getString(R.string.DELIVERY), null).apply();
                        FirebaseAuth.getInstance().signOut();
                        context.startActivity(new Intent(context, Login.class));

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
    public void  CACHE_VENDOR(User user, AppCompatActivity context, String tag, ProgressBar progressBar) {
        String area = new Gson().toJson(user);
        init(context).edit().putString(tag, area).apply();
        context.startActivity(new Intent(context, MainActivity.class));
        progressBar.setVisibility(View.INVISIBLE);


    }


    //Get Sign in Vendor.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public User GET_DELIVERY_CACHED(Context view, String tag) {
        String arrayListString = init(view).getString(tag, null);
        Type type = new TypeToken<User>() {
        }.getType();
        return new Gson().fromJson(arrayListString, type);

    }


    //IMG LOAD PROGRESS
    public void IMG(CircleImageView poster_value, String url, ProgressBar progressBar) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(poster_value.getContext()).load(url)
                .error(R.drawable.ic_baseline_error_outline_24)
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


    public User SERVE(String decor, String name, String img) {
        User u = new User();
        u.setName(decor);
        u.setImg_url(img);
        u.setUsername(name.replace("Vendor:", ""));
        return u;
    }


    public BitmapDescriptor return_bit_from_url(int img, Context context) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, img);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    public void APPLY(Context context, int i) {
        Log.d("TAGUTILS", "APPLY: " + i);
        if (i != -1)
            new utils().init(context.getApplicationContext()).edit().putString(context.getString(R.string.OPENED_ORDERS_COUNT), String.valueOf(i)).apply();
    }

}
