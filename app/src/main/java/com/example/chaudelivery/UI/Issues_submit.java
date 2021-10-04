package com.example.chaudelivery.UI;

import androidx.appcompat.app.AppCompatActivity;

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

        FirebaseFirestore.getInstance().collection("Issues")
                .document().set(MAP()).addOnCompleteListener(u -> {
            if (u.isSuccessful())
                new utils().message2("Issues received so sorry for the inconvenience, we are looking into it right away", this);
            else
                new utils().message2(" Error occurred while submitting Issues " + u.getException(), this);


        });
    }

    private Map<String, Object> MAP() {
        Map<String, Object> o = new HashMap<>();
        o.put("issue_user_email", issues_reporter_email.getText().toString());
        o.put("issues", issues_describe.getText().toString());
        return o;
    }
}