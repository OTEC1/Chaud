package com.example.chaudelivery.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chaudelivery.R;
import com.example.chaudelivery.utils.utils;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.chaudelivery.utils.Constant.Time_lapsed;

public class Login extends AppCompatActivity {

    private Button login, foget_pass;
    private TextView register;
    private EditText email, pass;
    private ProgressBar progressBar;


    private long back_pressed = 2000;

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
            if (!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(j -> {
                            if (j.isSuccessful())
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                             else {
                                new utils().message2("Error Occurred " + j.getException(), this);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
            } else
                new utils().message("Pls Fill out all fields", getApplicationContext());
        });


        register.setOnClickListener(a -> {
            startActivity(new Intent(getApplicationContext(), Register.class));
        });


        foget_pass.setOnClickListener(a -> {
            startActivity(new Intent(getApplicationContext(), Forgot_Pass.class));
        });
    }


    @Override
    public void onBackPressed() {

        new utils().message("Press twice to exit", getApplicationContext());
        if (back_pressed + Time_lapsed > System.currentTimeMillis()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        back_pressed = System.currentTimeMillis();
    }
}