package com.example.chaudelivery.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.chaudelivery.R;
import com.example.chaudelivery.utils.utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Issues_submit extends AppCompatActivity {

    private EditText issues_describe, issues_reporter_email;
    private Button submit_issues;
    private  ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues_submit);

        issues_reporter_email = (EditText) findViewById(R.id.issues_report_email);
        issues_describe = (EditText) findViewById(R.id.issues_describe);
        submit_issues = (Button) findViewById(R.id.submit_issues);

        if (FirebaseAuth.getInstance().getUid() != null) {
            issues_reporter_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            issues_describe.requestFocus();
        }


        submit_issues.setOnClickListener(g -> {
            if (!issues_reporter_email.getText().toString().trim().isEmpty() && !issues_describe.getText().toString().trim().isEmpty())
                POST_ISSUES();
        });


    }

    private void POST_ISSUES() {
        progressD(this).show();
        FirebaseFirestore.getInstance().collection("Issues")
                .document().set(MAP()).addOnCompleteListener(u -> {
            if (u.isSuccessful()) {
                new utils().message2("Sorry for the inconvenience, A ticket has been Opened", this);
                progressDialog.dismiss();
                issues_describe.setText("");
                issues_reporter_email.setText("");
            }else {
                new utils().message2(" Error occurred while submitting Issues " + u.getException(), this);
                progressDialog.dismiss();
            }
        });
    }

    private Map<String, Object> MAP() {
        Map<String, Object> o = new HashMap<>();
        o.put("issue_user_email", issues_reporter_email.getText().toString());
        o.put("issues", issues_describe.getText().toString());
        o.put("Status", true);
        if (getIntent().getStringExtra("order_id") != null)
            o.put("order_id", getIntent().getStringExtra("order_id"));
        else
            o.put("order_id", "");
        return o;
    }



    public ProgressDialog progressD(AppCompatActivity compatActivity) {
        progressDialog = new ProgressDialog(compatActivity);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return progressDialog;
    }
}