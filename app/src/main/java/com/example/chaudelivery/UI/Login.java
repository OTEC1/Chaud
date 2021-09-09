package com.example.chaudelivery.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chaudelivery.R;
import com.example.chaudelivery.utils.User;
import com.example.chaudelivery.utils.utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.chaudelivery.utils.Constant.Time_lapsed;

public class Login extends AppCompatActivity {

    private Button login, foget_pass;
    private TextView register;
    private EditText email, pass;
    private ProgressBar progressBar;


    private long back_pressed = 2000;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.email_sign_in_button);
        register = (TextView) findViewById(R.id.link_register);
        foget_pass = (Button) findViewById(R.id.forgot_pass);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);


        login.setOnClickListener(a -> {
            if (FirebaseAuth.getInstance().getUid() == null)
                if (!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnCompleteListener(j -> {
                                if (j.isSuccessful())
                                    QUICK_DOC_REF();
                                else {
                                    new utils().message2("Error Occurred " + j.getException(), this);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                } else
                    new utils().message("Pls Fill out all fields", getApplicationContext());
            else
                new utils().message("Pls Sign out", getApplicationContext());

        });


        register.setOnClickListener(a -> {
            startActivity(new Intent(getApplicationContext(), Register.class));
        });


        foget_pass.setOnClickListener(a -> {
            startActivity(new Intent(getApplicationContext(), Forgot_Pass.class));
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void QUICK_DOC_REF() {
        Map<String, Object> i = new HashMap<>();
        i.put("token", new utils().init(getApplicationContext()).getString(getString(R.string.DEVICE_TOKEN), ""));
        RE_USE(0, i, Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void FINAL_DOC_REF() {
        //Already installation
        if (new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_DELIVERY), null) != null) {
            //Another user sign in on device
            if (!Objects.equals(FirebaseAuth.getInstance().getUid(), new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_DELIVERY), null))) {
                Map<String, Object> i = new HashMap<>();
                i.put("token", "");
                RE_USE(1, i, new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_DELIVERY), null));
                //Same user sign in again
            } else
                NAVIGATE_USER();
            //First time installation
        } else
            NAVIGATE_USER();

    }

    private void NAVIGATE_USER() {
        new utils().USER_DATA(getApplicationContext());
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void RE_USE(int i, Map<String, Object> s, String doc_id) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(getString(R.string.DELIVERY_REG)).document(doc_id);
        documentReference.update(s).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (i != 1)
                    FINAL_DOC_REF();
                NAVIGATE_USER();

            } else {
                new utils().message("Account not Registered " + task.getException(), getApplicationContext());
                progressBar.setVisibility(View.GONE);
            }


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


//     progressBar.setVisibility(View.INVISIBLE);
//     context.startActivity(new Intent(context, MainActivity.class));
//     CACHE_VENDOR(null, context, 0, context.getString(R.string.VENDOR), progressBar);
}