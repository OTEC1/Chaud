package com.example.chaudelivery.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.chaudelivery.Adapter.Live_orders;
import com.example.chaudelivery.R;
import com.example.chaudelivery.model.User;
import com.example.chaudelivery.utils.Constant;
import com.example.chaudelivery.utils.utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class account extends Fragment {

    private TextView name, phone, address, email;
    private Button bad, report;
    private RatingBar fair_and_good;
    private CircleImageView circleImageView;
    private ProgressBar progressBar,progressBar1;



    private RecyclerView recyclerView;
    private Live_orders live_orders;
    private List<Map<String,Object>> os;



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
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar8);
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
        recyclerView = (RecyclerView) view.findViewById(R.id.completed_orders);


        if (FirebaseAuth.getInstance().getUid() != null) {
            LOAD_IN_DELIVERY_DETAILS();
            api_call();
        }
        else
            new utils().message2("Pls Sign in", requireActivity());


        report.setOnClickListener(s -> {
            startActivity(new Intent(getContext(), Issues_submit.class));
        });
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void LOAD_IN_DELIVERY_DETAILS() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            User user = new utils().GET_DELIVERY_CACHED(getContext(), getString(R.string.DELIVERY));
            name.setText("Name: " + user.getName());
            phone.setText("Phone: " + user.getPhone());
            address.setText("Address: " + user.getDelivery_details());
            email.setText("Email: " + user.getEmail());
            BAD_COUNT(user);
            new utils().IMG(circleImageView, Constant.IMG_URL.concat(user.getImg_url()), progressBar);
        }

    }

    private void BAD_COUNT(User user) {
        FirebaseFirestore.getInstance().collection(getString(R.string.DELIVERY_REG)).document(user.getUser_id())
                .addSnapshotListener((value, error) -> {
                    if (FirebaseAuth.getInstance().getUid() != null) {
                        assert value != null;
                        if (value.exists())
                            bad.setText("Bad review count: " + (value.getData().get("bad")));

                    }
                });
    }

    private void api_call() {
        FirebaseFirestore.getInstance().collection(getString(R.string.DISPATCHED_ORDERS)).document(FirebaseAuth.getInstance().getUid())
                .collection("orders").orderBy("Timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    os = new ArrayList<>();
                    assert value != null;
                    List<DocumentSnapshot> obj = value.getDocuments();
                    for (DocumentSnapshot d : obj) {
                        if(d.getBoolean("Received")) {
                            Map<String, Object> g = d.getData();
                            os.add(g);
                        }
                    }
                    setLayout(os);
                });
    }

    private void setLayout(List<Map<String, Object>> obj) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        live_orders = new Live_orders(getContext(), obj,1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(live_orders);
        progressBar1.setVisibility(View.GONE);
    }




}