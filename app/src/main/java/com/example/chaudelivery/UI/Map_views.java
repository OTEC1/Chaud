package com.example.chaudelivery.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.chaudelivery.Adapter.Live_orders;
import com.example.chaudelivery.R;
import com.example.chaudelivery.model.User;
import com.example.chaudelivery.utils.Constant;
import com.example.chaudelivery.utils.UserLocation;
import com.example.chaudelivery.utils.utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.example.chaudelivery.utils.Constant.MAP_KEY;

public class Map_views extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private LatLng lat;
    private Marker marker;
    private MapView mapView;
    private GoogleMap mgoogleMap;
    private GoogleApiClient apiClient;
    private MarkerOptions markerOptions = new MarkerOptions();
    private Button dialer;


    private List<UserLocation> userlocations = new ArrayList<>();
    private List<MarkerOptions> markerOptionslist = new ArrayList<>();
    private List<LatLng> latLngs = new ArrayList<>();


    private LocationRequest mLocationRequest;
    private String TAG = "Map_hold";
    private User user = new User();
    private int counts = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_views);
        mapView = findViewById(R.id.map_display_order_engagement);
        dialer = findViewById(R.id.call);

        if (getIntent().getExtras() != null) {
            String c = getIntent().getStringExtra("GEO_POINTS");
            Type type = new TypeToken<List<UserLocation>>() {
            }.getType();
            userlocations.addAll(new Gson().fromJson(String.valueOf(c), type));
        }
        init_GM(savedInstanceState);


        dialer.setOnClickListener(w -> {
            start_dialer(Constant.VENDOR_NO, getApplicationContext());
        });
    }


    public void start_dialer(String number, Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: " + number));
        context.startActivity(intent);
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
        buildGoogleApiClient();
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


    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        apiClient.connect();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {


        lat = new LatLng(location.getLatitude(), location.getLongitude());
        user.setName("");
        user.setImg_url("");
        user.setUsername(FirebaseAuth.getInstance().getUid());

        UserLocation userlocation = new UserLocation(new GeoPoint(lat.latitude, lat.longitude), null, user);
        userlocations.add(userlocation);


        if (marker != null)
            marker.remove();
        addMapMarkers(userlocations);

    }


    private void addMapMarkers(List<UserLocation> user_locations) {
        Log.d(TAG, "addMapMarkers:    " + user_locations);

        for (UserLocation user : user_locations) {
            String snippet = "";
            latLngs.add(new LatLng(user.getGeo_point().getLatitude(), user.getGeo_point().getLongitude()));
            try {
                if (user.getUser().getUsername().equals(FirebaseAuth.getInstance().getUid())) {
                    snippet = "My current Location";
                    markerOptions.icon(new utils().return_bit_from_url(R.drawable.ic_baseline_electric_bike_24, this));
                    markerOptions.position(new LatLng(latLngs.get(latLngs.size() - 1).latitude, latLngs.get(latLngs.size() - 1).longitude));
                } else if (user.getUser().getName().equals("Dropoff")) {
                    snippet = user.getUser().getUsername() + " current Location";
                    markerOptions.icon(new utils().return_bit_from_url(R.drawable.ic_baseline_account_location, this));
                    markerOptions.position(new LatLng(latLngs.get(1).latitude, latLngs.get(1).longitude));
                } else if (user.getUser().getName().equals("Pickup")) {
                    snippet = user.getUser().getUsername() + " current Location";
                    markerOptions.icon(new utils().return_bit_from_url(R.drawable.ic_baseline_storefront_24, this));
                    markerOptions.position(new LatLng(latLngs.get(0).latitude, latLngs.get(0).longitude));
                }

                markerOptions.title(snippet);
                marker = mgoogleMap.addMarker(markerOptions);
                markerOptionslist.addAll(Collections.singleton(markerOptions));
                SetCameraView(lat);

            } catch (Exception e) {
                Log.d(TAG, "Error Occurred while adding Markers: " + e.getLocalizedMessage());
            }
        }


    }


    private void SetCameraView(LatLng lat) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(lat, 15);
        mgoogleMap.animateCamera(update);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, mLocationRequest, this::onLocationChanged);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}