package com.example.chaudelivery.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chaudelivery.R;
import com.example.chaudelivery.Retrofit_.Base_config;
import com.example.chaudelivery.Retrofit_.Request_class;
import com.example.chaudelivery.utils.utils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Forgot_Pass extends AppCompatActivity {


    private Button button2;
    private EditText email;
    private ProgressBar progressBar;

    private static long back_pressed;
    private static int Time_lapsed = 2000;
    private String TAG= "Forgot_Pass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        button2= findViewById(R.id.email_sign_in_button);
        progressBar= findViewById(R.id.progress);
        email= findViewById(R.id.email);

        button2.setOnClickListener(view ->{
            if(email.getText().toString().trim().isEmpty())
                new utils().message("Pls fill out the email field", getApplicationContext());
            else {
                progressBar.setVisibility(View.VISIBLE);
                button2.setEnabled(false);
                FIREBASE_CALL();
            }
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


    private void FIREBASE_CALL() {

        Map<String, Object> mail = new HashMap<>();
        mail.put("youtubeLink", email.getText().toString());
        Request_class request_class = Base_config.getRetrofit().create(Request_class.class);
        Call<Object> call = request_class.sendRestLink(mail);

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.body() == null)
                    new utils().Long_message("Sorry This Email Doesn't have an account with us !", getApplicationContext());
                else
                    new utils().Long_message("A Password Reset Link would be Sent to your email", getApplicationContext());
                progressBar.setVisibility(View.INVISIBLE);
                button2.setEnabled(true);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                new utils().message("An Error Occurred " + t.getLocalizedMessage(), getApplicationContext());
                button2.setEnabled(true);
            }
        });


    }




}