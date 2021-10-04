package com.example.chaudelivery.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chaudelivery.R;
import com.example.chaudelivery.model.User;
import com.example.chaudelivery.utils.UserLocation;
import com.example.chaudelivery.utils.utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.chaudelivery.utils.Constant.Time_lapsed;

public class Login extends AppCompatActivity {

    private Button login, foget_pass;
    private TextView register, issues;
    private EditText email, pass;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    private User user;
    Map<String, Object> token;


    private long back_pressed = 2000;
    private String TAG = "Login";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.email_sign_in_button);
        register = (TextView) findViewById(R.id.link_register);
        foget_pass = (Button) findViewById(R.id.forgot_pass);
        issues = (TextView) findViewById(R.id.issues);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        firebaseFirestore = FirebaseFirestore.getInstance();
        token = new HashMap<>();


        login.setOnClickListener(a -> {
            if (!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty())
                signIn();
            else
                new utils().message2("Pls fill out both fields", this);


        });


        register.setOnClickListener(a -> {
            startActivity(new Intent(getApplicationContext(), Register.class));
        });


        foget_pass.setOnClickListener(a -> {
            startActivity(new Intent(getApplicationContext(), Forgot_Pass.class));
        });

        issues.setOnClickListener(k -> {
            startActivity(new Intent(getApplicationContext(), Issues_submit.class));
        });
    }


    //Step 1
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        RUNCHECK(new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_DELIVERY), ""));
                    else
                        message("Failed " + task.getException());
                }).addOnFailureListener(e -> {
            message("Authentication Failed" + e.getLocalizedMessage());
            hideDialog();
        });


    }


    private void RUNCHECK(String last_signed_in_user) {
        Log.d(TAG, "RUNCHECK: " + last_signed_in_user);
        if (!FirebaseAuth.getInstance().getUid().equals(last_signed_in_user))
            firebaseFirestore.collection(getString(R.string.DELIVERY_LOCATION)).get().addOnCompleteListener(s -> {
                if (s.isSuccessful())
                    if (s.getResult().getDocuments().size() > 0 && last_signed_in_user.trim().length() > 0)
                        CLEAR_LAST_SIGN_IN_USER_TOKEN_LOCATION(last_signed_in_user);
                    else
                        GET_SIGNED_IN_USER_DETAILS();

                else
                    new utils().message2("Error occurred " + s.getException(), this);
            });
        else
            GET_SIGNED_IN_USER_DETAILS();

    }

    private void CLEAR_LAST_SIGN_IN_USER_TOKEN_LOCATION(String last_signed_in_user) {

        firebaseFirestore.collection(getString(R.string.DELIVERY_LOCATION)).document(last_signed_in_user).get().addOnCompleteListener(j -> {
            if (j.isSuccessful()) {
                if (j.getResult().exists())
                    NEXT(last_signed_in_user);
            } else
                new utils().message2("Error occurred on reading Doc" + j.getException(), this);
        });


    }

    private void NEXT(String last_signed_in_user) {

        token = new HashMap<>();
        firebaseFirestore.collection(getString(R.string.DELIVERY_LOCATION)).document(last_signed_in_user)
                .get().addOnCompleteListener(d -> {
            if (d.isSuccessful()) {
                UserLocation muser = d.getResult().toObject(UserLocation.class);
                token.put("bad", muser.getUser().getBad());
                token.put("delivery_details", muser.getUser().getDelivery_details());
                token.put("email", muser.getUser().getEmail());
                token.put("fair", muser.getUser().getFair());
                token.put("good", muser.getUser().getGood());
                token.put("img_url", muser.getUser().getImg_url());
                token.put("member_T", muser.getUser().getMember_T());
                token.put("name", muser.getUser().getName());
                token.put("phone", muser.getUser().getPhone());
                token.put("token", "");
                token.put("user_id", muser.getUser().getUser_id());
                token.put("username", muser.getUser().getUsername());


                firebaseFirestore.collection(getString(R.string.DELIVERY_LOCATION)).document(last_signed_in_user)
                        .update("user", token).addOnCompleteListener(i -> {
                    if (i.isSuccessful())
                        CLEAR_LAST_SIGN_IN_USER_TOKEN_REGISTRATION(last_signed_in_user);
                    else
                        new utils().message2("Error occurred " + i.getException(), this);
                });
            }
        });


    }


    private void CLEAR_LAST_SIGN_IN_USER_TOKEN_REGISTRATION(String last_signed_in_user) {
        token = new HashMap<>();
        if (last_signed_in_user.trim().length() > 0) {
            token.put("token", "");
            firebaseFirestore.collection(getString(R.string.DELIVERY_REG)).document(last_signed_in_user)
                    .update(token).addOnCompleteListener(i -> {
                if (i.isSuccessful())
                    GET_SIGNED_IN_USER_DETAILS();
                else
                    new utils().message2("Error occurred " + i.getException(), this);
            });
        } else
            GET_SIGNED_IN_USER_DETAILS();
    }


    private void GET_SIGNED_IN_USER_DETAILS() {
        firebaseFirestore.collection(getString(R.string.DELIVERY_REG)).document(FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(y -> {
            if (y.isSuccessful())
                new utils().CACHE_VENDOR(y.getResult().toObject(User.class), getApplicationContext(), 0, getString(R.string.DELIVERY), progressBar);
            else
                new utils().message2("Error occurred " + y.getException(), this);
        });
    }


    @Override
    public void onBackPressed() {

        new utils().message2("Press twice to exit", this);
        if (back_pressed + Time_lapsed > System.currentTimeMillis()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        back_pressed = System.currentTimeMillis();
    }

    private void message(String s) {
        new utils().message2(s, this);
    }


    private void hideDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }


//     progressBar.setVisibility(View.INVISIBLE);
//     context.startActivity(new Intent(context, MainActivity.class));
//     CACHE_VENDOR(null, context, 0, context.getString(R.string.VENDOR), progressBar);
}