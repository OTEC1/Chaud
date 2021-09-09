package com.example.chaudelivery.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.chaudelivery.R;
import com.example.chaudelivery.utils.Constant;
import com.example.chaudelivery.utils.UserLocation;
import com.example.chaudelivery.utils.utils;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;


public class account extends Fragment {


    //------------check if sign in   perform business logic-------------------------//
    private TextView name, phone, address, email;
    private Button bad, report, available;
    private RatingBar fair_and_good;
    private CircleImageView circleImageView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        name = (TextView) view.findViewById(R.id.Name);
        phone = (TextView) view.findViewById(R.id.phone);
        address = (TextView) view.findViewById(R.id.address);
        email = (TextView) view.findViewById(R.id.email);
        circleImageView = (CircleImageView) view.findViewById(R.id.sign_in_vendor_img);
        fair_and_good = (RatingBar) view.findViewById(R.id.fair_and_good);
        bad = (Button) view.findViewById(R.id.bad_review_count);
        report = (Button) view.findViewById(R.id.report);
        available = (Button) view.findViewById(R.id.available);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar8);


        if (FirebaseAuth.getInstance().getUid() != null)
            LOAD_IN_DELIVERY_DETAILS();
        else
            new utils().message2("Pls Sign in", requireActivity());

        return view;
    }

    private void LOAD_IN_DELIVERY_DETAILS() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            UserLocation user = new utils().GET_DELIVERY_CACHED(getContext(), getString(R.string.VENDOR));
            name.setText("Name: "+user.getUser().getName());
            phone.setText("Phone: "+user.getUser().getPhone());
            address.setText("Address: "+user.getUser().getDelivery_details());
            email.setText("Email: "+user.getUser().getEmail());
            bad.setText("Bad review count: "+(user.getUser().getBad()));
            new utils().IMG(circleImageView, Constant.IMG_URL.concat(user.getUser().getImg_url()), progressBar);
        }

    }
}