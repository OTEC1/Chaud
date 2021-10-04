package com.example.chaudelivery.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.example.chaudelivery.R;
import com.example.chaudelivery.utils.UserLocation;
import com.example.chaudelivery.utils.utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import static com.example.chaudelivery.utils.Constant.MAP_KEY;

public class Map_views extends AppCompatActivity implements OnMapReadyCallback {


    private LatLng lat;
    private  Marker marker;
    private MapView mapView;
    private GoogleMap mgoogleMap;
    private MarkerOptions options = new MarkerOptions();
    private List<UserLocation> userLocations = new LinkedList<>();
    private String TAG = "Map_views";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_views);
        mapView = findViewById(R.id.map_display_order_engagement);

        if (getIntent().getExtras() != null) {
            String c = getIntent().getStringExtra("GEO_POINTS");
            Type type = new TypeToken<List<UserLocation>>() {}.getType();
            userLocations.addAll(new Gson().fromJson(String.valueOf(c), type));
            Log.d(TAG, "onCreate: " + userLocations+"   ");
        }
        init_GM(savedInstanceState);


    }


    private void init_GM(Bundle savedInstanceState) {
        Bundle mapviewBundle = null;
        if (savedInstanceState != null)
            mapviewBundle = savedInstanceState.getBundle(MAP_KEY);
        mapView.onCreate(mapviewBundle);
        mapView.getMapAsync(this);

    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        // startUserLocationsRunnable();

    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        //stopLocationUpdates();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        //stopLocationUpdates();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        //stopLocationUpdates();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        googleMap.setMyLocationEnabled(true);
        mgoogleMap = googleMap;
        addMarkers();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }


    private void addMarkers() {

        if (mgoogleMap != null) {
            for (UserLocation x : userLocations) {
                Log.d(TAG, "addMarkers: "+x.getUser().getName());
                try {

                    String snippet = "";
                    if (x.getUser().getName().equals("Delivery location")) {
                        snippet = x.getUser().getUsername() + "  current location";
                        options.icon(new utils().return_bit_from_url(R.drawable.ic_baseline_electric_bike_24,this));
                    }
                    else if (x.getUser().getName().equals("Drop off")) {
                        snippet = x.getUser().getUsername() + " Delivery Point";
                        options.icon(new utils().return_bit_from_url(R.drawable.ic_baseline_account_location,this));
                    }
                    else {
                        snippet = x.getUser().getUsername() + " Pick up point";
                        options.icon(new utils().return_bit_from_url(R.drawable.ic_baseline_storefront_24, this));
                    }

                    lat = new LatLng(x.getGeo_point().getLatitude(), x.getGeo_point().getLongitude());
                    options.title(snippet);
                    options.position(lat);
                    marker = mgoogleMap.addMarker(options);
                    SetCameraView(lat);
                } catch (Exception e) {
                    new utils().message("Error Occurred while adding map markers:" + e.getLocalizedMessage(), this);
                    Log.d(TAG, "addMarkers: "+e.getLocalizedMessage());
                }
            }

        }
    }


    private void SetCameraView(LatLng lat) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(lat, 15);
        mgoogleMap.animateCamera(update);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}