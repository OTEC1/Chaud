package com.example.chaudelivery.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chaudelivery.R;
import com.example.chaudelivery.utils.Constant;
import com.example.chaudelivery.utils.UserLocation;
import com.example.chaudelivery.utils.utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Accept_Order extends AppCompatActivity {

    private TextView muser_time_place, muser_phone, muser_order_id, muser_item_size, mvendor_name, mvendor_business_details, mvendor_phone;
    private Button accept, decline;
    private CircleImageView circleImageView;
    private ProgressBar progressBar;
    private DocumentReference doc, doc1;


    private String TAG = "Accept_Order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_order);


        muser_time_place = (TextView) findViewById(R.id.Time_placed);
        muser_phone = (TextView) findViewById(R.id.client_phone);
        muser_order_id = (TextView) findViewById(R.id.Order_id);
        muser_item_size = (TextView) findViewById(R.id.Order_items);

        mvendor_name = (TextView) findViewById(R.id.vendor_name);
        mvendor_business_details = (TextView) findViewById(R.id.Vendor_business_D);
        mvendor_phone = (TextView) findViewById(R.id.vendor_pick_up_phone);
        circleImageView = (CircleImageView) findViewById(R.id.vendor_img);
        progressBar = (ProgressBar) findViewById(R.id.progressBar8);
        accept = (Button) findViewById(R.id.accept_order);
        decline = (Button) findViewById(R.id.decline);


        CHECK_SIGNED_IN_DELIVERY();


        END_POINT();


        accept.setOnClickListener(j -> {

            if (FirebaseAuth.getInstance().getUid() != null) {
                ACCEPT_LOGIC(FirebaseAuth.getInstance().getUid());
            } else
                new utils().message2("Pls sign in", this);
        });


        decline.setOnClickListener(j -> {
            if (FirebaseAuth.getInstance().getUid() != null) {
                DECLINE_LOGIC(FirebaseAuth.getInstance().getUid());
            } else
                new utils().message2("Pls sign in", this);
        });

    }

    private void CHECK_SIGNED_IN_DELIVERY() {
        if (FirebaseAuth.getInstance().getUid() != null)
            POPULATE_WIDGET();
        else
            new utils().message2("Pls sign in", this);
    }

    private void END_POINT() {
        doc = FirebaseFirestore.getInstance().collection(getString(R.string.PAID_VENDORS_SECTION))
                .document("Orders").collection(getIntent().getStringExtra("Client_ID"))
                .document(getIntent().getStringExtra("doc_id_Gen"));

        doc1 = FirebaseFirestore.getInstance().collection(getString(R.string.PAID_VENDORS_SECTION))
                .document("Orders").collection(getIntent().getStringExtra("Client_ID")).document("1A").collection("A1")
                .document(getIntent().getStringExtra("Order_id"));
    }


    private void POPULATE_WIDGET() {

        muser_time_place.setText("Time: " + getIntent().getLongExtra("Timestamp", 0) + " minute Ago");
        muser_phone.setText("Phone: " + getIntent().getStringExtra("Drop_off_phone_no") + "  ");
        muser_order_id.setText(getIntent().getStringExtra("Order_id") + " ");
        muser_item_size.setText((getIntent().getIntExtra("Order_items", 0) > 1) ? "Package Size: " + getIntent().getIntExtra("Order_items", 0) + " items" : "Package Size: " + getIntent().getIntExtra("Order_items", 0) + " item");
        mvendor_name.setText(getIntent().getStringExtra("Vendor") + " ");
        mvendor_business_details.setText("Pick up: " + getIntent().getStringExtra("Vendor_business_D") + " ");
        mvendor_phone.setText("Phone no: " + getIntent().getStringExtra("Vendor_Phone") + " ");
        new utils().IMG(circleImageView, Constant.IMG_URL + getIntent().getStringExtra("Vendor_img_url"), progressBar);

        Log.d(TAG, "POPULATE_WIDGET: " + getIntent().getStringExtra("Drop_off_geo_point"));
        Log.d(TAG, "POPULATE_WIDGET: " + getIntent().getStringExtra("Pick_up_geo_point"));



    }


    private void ACCEPT_LOGIC(String delivery_uid) {
        doc1.get().addOnCompleteListener(h -> {
                    if (h.isSuccessful()) {
                        if (h.getResult().get("Delivery").equals("0000"))
                            UPDATE_COLUMN(delivery_uid);
                    } else if (h.getResult().getBoolean("DStatus"))
                        new utils().message2("Order already taken", this);
                    else
                        new utils().message2("Error Occurred", this);

                });
    }


    private void DECLINE_LOGIC(String uid) {

    }



    private void UPDATE_COLUMN(String delivery_uid) {

        //also update A1 1A//
        doc1.update(new utils().maps(delivery_uid,0)).addOnCompleteListener(o->{
            if(o.isSuccessful()) {
                Log.d(TAG, "UPDATE_COLUMN: 1");
                SECOND_UPDATE(delivery_uid);
            }
            else
                Log.d(TAG, "Error occurred while UPDATING_COLUMN: ");
        });
    }


    private void SECOND_UPDATE(String delivery_uid) {

        doc.update(new utils().maps(delivery_uid, 1)).addOnCompleteListener(o -> {
            if (o.isSuccessful()) {
                Log.d(TAG, "UPDATE_COLUMN: 2");
                CREATE_DELIVERY_ORDERS_TABLE();
            } else
                Log.d(TAG, "Error occurred while UPDATING_COLUMN: ");
        });
    }


    private void CREATE_DELIVERY_ORDERS_TABLE() {
        Map<String,Object> map = new HashMap<>();
        map.put("Vendor",getIntent().getStringExtra("Vendor"));
        map.put("Vendor_img_url", getIntent().getStringExtra("Vendor_img_url"));
        map.put("Vendor_ID",getIntent().getStringExtra("Vendor_ID"));
        map.put("Client_ID",getIntent().getStringExtra("Client_ID"));
        map.put("Vendor_Phone", getIntent().getStringExtra("Vendor_Phone"));
        map.put("Order_id", getIntent().getStringExtra("Order_id"));
        map.put("Order_items", getIntent().getIntExtra("Order_items",0));
        map.put("Vendor_business_D", getIntent().getStringExtra("Vendor_business_D"));
        map.put("Drop_off_phone_no", getIntent().getStringExtra("Drop_off_phone_no"));
        map.put("Timestamp", getIntent().getLongExtra("Timestamp",0));
        map.put("doc_id_Gen", getIntent().getStringExtra("doc_id_Gen"));


        FirebaseFirestore.getInstance().collection(getString(R.string.DISPATCHED_ORDERS)).document(FirebaseAuth.getInstance().getUid())
                .set(map).addOnCompleteListener(u->{
                    if(u.isSuccessful()) {
                        Log.d(TAG, "CREATE_DELIVERY_ORDERS_TABLE: ");


                        //new UserLocation().setGeo_point(LOOP_GEO(getIntent().getStringExtra("Drop_off_geo_point")));
                       // startActivity(new Intent(getApplicationContext(), Map_views.class).putExtra("geo_pont_pick_up"));
                    }
                    else
                        Log.d(TAG, "CREATE_DELIVERY_EXCEPTION: "+u.getException());

        });
    }

    private GeoPoint LOOP_GEO(String drop_off_geo_point) {

        return  null;
    }


}