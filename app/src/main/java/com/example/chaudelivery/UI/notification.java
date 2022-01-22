package com.example.chaudelivery.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.chaudelivery.Adapter.Live_orders;
import com.example.chaudelivery.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class notification extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Live_orders live_orders;


    private List<Map<String, Object>> os;
    private String TAG = "notification";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = view.findViewById(R.id.main_recycle_view);
        progressBar = view.findViewById(R.id.progressBar);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                api_call1();
        return view;
    }



    //Call client ID geo point & vendor id geo_point b4 list population
    private void api_call1() {
        FirebaseFirestore.getInstance().collection(getString(R.string.DISPATCHED_ORDERS)).document(FirebaseAuth.getInstance().getUid())
                     .collection("orders").orderBy("Timestamp", Query.Direction.DESCENDING)
                         .addSnapshotListener((value, error) -> {
                                if(FirebaseAuth.getInstance().getCurrentUser()!= null) {
                                    os = new ArrayList<>();
                                    assert value != null;
                                    List<DocumentSnapshot> obj = value.getDocuments();
                                    for (DocumentSnapshot d : obj) {
                                        if (!d.getBoolean("Received")) {
                                            Map<String, Object> g = d.getData();
                                            os.add(g);
                                        }
                                    }
                                    setLayout(os);
                                }
                         });
             }





    //SET LAYOUT
    private void setLayout(List<Map<String, Object>> obj) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        live_orders = new Live_orders(getContext(), obj,0);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(live_orders);
        progressBar.setVisibility(View.GONE);
    }
}