package com.example.chaudelivery.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chaudelivery.R;
import com.example.chaudelivery.model.User;
import com.example.chaudelivery.utils.Constant;
import com.example.chaudelivery.utils.UserLocation;
import com.example.chaudelivery.utils.utils;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.chaudelivery.utils.Constant.GPRS_INTERVAL;
import static com.example.chaudelivery.utils.Constant.UPDATE_INTERVAL;
import static com.example.chaudelivery.utils.Constant.latitude;
import static com.example.chaudelivery.utils.Constant.longitude;

public class Accept_Order extends AppCompatActivity {

    private TextView muser_time_place, muser_phone, muser_order_id, muser_item_size, mvendor_name, mvendor_business_details, mvendor_phone;
    private Button accept, decline;
    private CircleImageView circleImageView;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;


    private User user;
    private List<UserLocation> points;
    private double latitudes, longtitudes;


    private final String TAG = "AcceptOrder";
    private boolean start_service = true;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        start_service = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_order);
        muser_time_place = findViewById(R.id.Time_placed);
        muser_phone = findViewById(R.id.client_phone);
        muser_order_id = findViewById(R.id.Order_id);
        muser_item_size = findViewById(R.id.Order_items);
        mvendor_name = findViewById(R.id.vendor_name);
        mvendor_business_details = findViewById(R.id.Vendor_business_D);
        mvendor_phone = findViewById(R.id.vendor_pick_up_phone);
        circleImageView = findViewById(R.id.vendor_img);
        progressBar = findViewById(R.id.progressBar8);
        accept = findViewById(R.id.accept_order);
        decline = findViewById(R.id.decline);
        points = new ArrayList<>();
        getLast_know_Location();
        CHECK_SIGNED_IN_DELIVERY();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            user = call(getApplicationContext());


        accept.setOnClickListener(j -> {

            if (FirebaseAuth.getInstance().getUid() != null) {
                progressD().show();
                ACCEPT_LOGIC();
            } else {
                new utils().message2("Pls sign in", this);
                progressDialog.dismiss();
            }
        });


        decline.setOnClickListener(j -> {
            if (FirebaseAuth.getInstance().getUid() != null) {
                DECLINE_LOGIC(FirebaseAuth.getInstance().getUid());
            } else
                new utils().message2("Pls sign in", this);
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public User call(Context applicationContext) {
        return new utils().GET_DELIVERY_CACHED(applicationContext, applicationContext.getString(R.string.DELIVERY));
    }


    private void CHECK_SIGNED_IN_DELIVERY() {
        if (FirebaseAuth.getInstance().getUid() != null)
            POPULATE_WIDGET();
        else
            new utils().message2("Pls sign in", this);
    }


    @SuppressLint("SetTextI18n")
    private void POPULATE_WIDGET() {
        muser_time_place.setText("Time: " + getIntent().getLongExtra("Timestamp", 0) + " minute Ago");
        muser_phone.setText("Phone: " + getIntent().getStringExtra("Drop_off_phone_no") + "  ");
        muser_order_id.setText(getIntent().getStringExtra("Order_id") + " ");
        muser_item_size.setText((getIntent().getIntExtra("Order_items", 0) > 1) ? "Package Size: " + getIntent().getIntExtra("Order_items", 0) + " items" : "Package Size: " + getIntent().getIntExtra("Order_items", 0) + " item");
        mvendor_name.setText(getIntent().getStringExtra("Vendor") + " ");
        mvendor_business_details.setText("Pick up: " + getIntent().getStringExtra("Vendor_business_D") + " ");
        mvendor_phone.setText("Phone no: " + getIntent().getStringExtra("Vendor_Phone") + " ");
        new utils().IMG(circleImageView, Constant.IMG_URL + getIntent().getStringExtra("Vendor_img_url"), progressBar);


    }


    private void ACCEPT_LOGIC() {
        CollectionReference x = FirebaseFirestore.getInstance().collection(getString(R.string.PAID_VENDORS_SECTION))
                .document("Orders").collection(getIntent().getStringExtra("Client_ID"));
        x.get().addOnCompleteListener(h -> {
            if (h.isSuccessful()) {
                List<DocumentSnapshot> m = h.getResult().getDocuments();
                for (DocumentSnapshot n : m) {
                    if (n.getId().equals(getIntent().getStringExtra("doc_id_Gen")))
                        if (!n.getBoolean("DStatus"))
                            ADD_DELIVERY_TO_ORDER();
                        else {
                            new utils().message("Order  already taken", this);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            progressDialog.dismiss();
                        }
                }
            } else {
                progressDialog.dismiss();
                new utils().message2("Error Occurred" + h.getException(), this);
            }
        });
    }


    private void ADD_DELIVERY_TO_ORDER() {

        FirebaseFirestore.getInstance().collection(getString(R.string.PAID_VENDORS_SECTION)).document("Orders").collection(getIntent().getStringExtra("Client_ID")).document("1A").collection("A1").document(getIntent().getStringExtra("Order_id")).get().addOnCompleteListener(h -> {
            if (h.isSuccessful()) {
                DocumentSnapshot maps = h.getResult();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    if (maps.get("Delivery").toString().equals("0000")) {
                        UPDATE_COLUMN(FirebaseAuth.getInstance().getUid());
                    } else {
                        progressDialog.dismiss();
                        new utils().message("Request failed  Order already taken", this);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }

            } else {
                new utils().message2("Error Occurred " + h.getException(), this);
                progressDialog.dismiss();
            }

        });
    }


    private void UPDATE_COLUMN(String delivery_uid) {

        //also update A1 1A//
        FirebaseFirestore.getInstance().collection(getString(R.string.PAID_VENDORS_SECTION)).document("Orders").collection(getIntent().getStringExtra("Client_ID")).document("1A").collection("A1").document(getIntent().getStringExtra("Order_id")).update(new utils().maps(delivery_uid, 0)).addOnCompleteListener(o -> {
            if (o.isSuccessful()) {
                SECOND_UPDATE(delivery_uid);
            } else {
                Log.d(TAG, "Error occurred while  " + o.getException());
                progressDialog.dismiss();
            }
        });
    }


    private void SECOND_UPDATE(String delivery_uid) {

        FirebaseFirestore.getInstance().collection(getString(R.string.PAID_VENDORS_SECTION)).document("Orders").collection(getIntent().getStringExtra("Client_ID")).document(getIntent().getStringExtra("doc_id_Gen")).update(new utils().maps(delivery_uid, 1)).addOnCompleteListener(o -> {
            if (o.isSuccessful()) {
                CREATE_DELIVERY_ORDERS_TABLE();
            } else {
                Log.d(TAG, "Error occurred " + o.getException());
                progressDialog.dismiss();
            }
        });
    }


    private void CREATE_DELIVERY_ORDERS_TABLE() {
        if (latitude != 0 && longitude != 0) {
            points.add(LOOP_GEO(getIntent().getExtras().getString("Pick_up_geo_point"), "Pickup", getIntent().getStringExtra("user_img_url"), getIntent().getStringExtra("Client_name")));
            points.add(LOOP_GEO(getIntent().getStringExtra("Drop_off_geo_point"), "Dropoff", getIntent().getStringExtra("Vendor_img_url"), getIntent().getStringExtra("Vendor")));
            points.add(LOOP_GEO("Delivery", "Delivery location", user.getImg_url(), user.getName()));
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection(getString(R.string.DISPATCHED_ORDERS)).document(FirebaseAuth.getInstance().getUid()).collection("orders").document();
            documentReference.set(map(documentReference.getId())).addOnCompleteListener(u -> {
                if (u.isSuccessful())
                    if (points.size() == 3) {
                        startActivity(new Intent(getApplicationContext(), Map_views.class).putExtra("GEO_POINTS", new Gson().toJson(points)));
                        LOCAL_CACHE_OPENED_ORDERS_COUNT(1);
                    } else
                        new utils().message2("List not complete ! ", this);
                else {
                    Log.d(TAG, "CREATE_DELIVERY_EXCEPTION: " + u.getException());
                    progressDialog.dismiss();
                }

            });
        } else
            new utils().message2("GeoPoint is null ", this);
    }

    private void LOCAL_CACHE_OPENED_ORDERS_COUNT(int i) {

        if (new utils().init(getApplicationContext()).getString(getString(R.string.OPENED_ORDERS_COUNT), null) != null)
            new utils().APPLY(getApplicationContext(), i + Integer.parseInt(new utils().init(getApplicationContext()).getString(getString(R.string.OPENED_ORDERS_COUNT), null)));
        else
            new utils().APPLY(getApplicationContext(), i);
    }


    private Map<String, Object> map(String id) {

        Map<String, Object> map = new HashMap<>();
        map.put("Client_ID", getIntent().getStringExtra("Client_ID"));
        map.put("Client_name", getIntent().getStringExtra("Client_name"));
        map.put("doc_id_Gen", getIntent().getStringExtra("doc_id_Gen"));
        map.put("doc_created_id", id);
        map.put("Order_id", getIntent().getStringExtra("Order_id"));
        map.put("Order_items", getIntent().getIntExtra("Order_items", 0));
        map.put("user_img_url", getIntent().getStringExtra("user_img_url"));
        map.put("Drop_off_phone_no", getIntent().getStringExtra("Drop_off_phone_no"));
        map.put("Pick_up_geo_point", Geo(getIntent().getExtras().getString("Pick_up_geo_point")));
        map.put("Timestamp", getIntent().getLongExtra("Timestamp", 0));
        map.put("Drop_off_geo_point", Geo(getIntent().getStringExtra("Drop_off_geo_point")));
        map.put("Vendor_ID", getIntent().getStringExtra("Vendor_ID"));
        map.put("Vendor_Phone", getIntent().getStringExtra("Vendor_Phone"));
        map.put("Vendor", getIntent().getStringExtra("Vendor"));
        map.put("Vendor_img_url", getIntent().getStringExtra("Vendor_img_url"));
        map.put("Vendor_business_D", getIntent().getStringExtra("Vendor_business_D"));
        return map;
    }


    public UserLocation LOOP_GEO(String drop_off, String decor, String img_url, String name) {

        if (!drop_off.equals("Delivery")) {
            latitudes = Double.parseDouble(drop_off.substring(drop_off.indexOf(":") + 1, drop_off.indexOf(",")));
            longtitudes = Double.parseDouble(drop_off.substring(drop_off.lastIndexOf(":") + 1, drop_off.indexOf("}")));
        }
        return new UserLocation(new GeoPoint(latitudes, longtitudes), null, new utils().SERVE(decor, name,img_url));
    }


    private GeoPoint Geo(String drop_off) {
        latitudes = Double.parseDouble(drop_off.substring(drop_off.indexOf(":") + 1, drop_off.indexOf(",")));
        longtitudes = Double.parseDouble(drop_off.substring(drop_off.lastIndexOf(":") + 1, drop_off.indexOf("}")));
        return new GeoPoint(latitudes, longtitudes);
    }


    private void getLast_know_Location() {
        Log.d(TAG, " requesting for last known location");

        if (start_service) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(GPRS_INTERVAL);
            mLocationRequest.setFastestInterval(UPDATE_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (locationResult == null)
                        return;

                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            };
            LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }


    public ProgressDialog progressD() {
        progressDialog = new ProgressDialog(Accept_Order.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return progressDialog;
    }


    private void DECLINE_LOGIC(String uid) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        if (new utils().init(getApplicationContext()).getString(getString(R.string.OPENED_ORDERS_COUNT), null) != null)
            if (Integer.parseInt(new utils().init(getApplicationContext()).getString(getString(R.string.OPENED_ORDERS_COUNT), null)) != 0)
                FirebaseFirestore.getInstance().collection(getString(R.string.DISPATCHED_ORDERS_DELCINE)).document().set(MAP(uid))
                        .addOnCompleteListener(h -> {
                            if (!h.isSuccessful())
                                Log.d(TAG, "Error occurred " + h.getException());
                            else
                                Log.d(TAG, "DECLINE_LOGIC: SENT");
                        });

    }


    private Map<String, Object> MAP(String uid) {
        return null;
    }


}