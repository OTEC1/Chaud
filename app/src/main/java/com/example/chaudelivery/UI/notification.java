package com.example.chaudelivery.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chaudelivery.R;
import com.google.firebase.firestore.FirebaseFirestore;


public class notification extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        api_call1();

        return view;
    }

    private void api_call1() {
        FirebaseFirestore.getInstance().collection("").document().get()
                .addOnCompleteListener(u->{

                });
    }
}