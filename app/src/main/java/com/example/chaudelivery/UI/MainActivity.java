package com.example.chaudelivery.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.example.chaudelivery.R;
import com.example.chaudelivery.Running_Service.Keep_alive;
import com.example.chaudelivery.Running_Service.PushReceiver;
import com.example.chaudelivery.Running_Service.RegisterUser;
import com.example.chaudelivery.utils.User;
import com.example.chaudelivery.utils.UserLocation;
import com.example.chaudelivery.utils.utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

import me.pushy.sdk.Pushy;

import static com.example.chaudelivery.utils.Constant.ERROR_DIALOG_REQUEST;
import static com.example.chaudelivery.utils.Constant.GPRS_INTERVAL;
import static com.example.chaudelivery.utils.Constant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.chaudelivery.utils.Constant.PERMISSIONS_REQUEST_ENABLE_GPS;
import static com.example.chaudelivery.utils.Constant.READ_STORAGE_PERMISSION_REQUEST_CODE;
import static com.example.chaudelivery.utils.Constant.UPDATE_INTERVAL;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Task<DocumentSnapshot> task1;
    private UserLocation muserLocation;
    private SharedPreferences sp;
    private  Keep_alive keep_alive;
    private  Intent intent;

    private  String TAG ="MainActivity";
    private  boolean mLocationPermissionGranted = false;



    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getUid() != null) {
        new utils().openFragment(new notification(),"notification",1,this);
            if (checkMapServices()) {
                if (mLocationPermissionGranted) {
                    getUserDetails();
                } else
                    getLocationPermission();
            }
        } else
            new utils().message2("Pls sign in", this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getUid() ==null)
            startActivity(new Intent(getApplicationContext(),Login.class));

        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNav);
        new utils().bottom_nav(bottomNavigationView,this,sp);
        NOTIFICATION_LISTER();
        POLICY_SERVICE();
        CHECK_FOR_PERMISSION();
    }





    private void NOTIFICATION_LISTER() {

        keep_alive = new Keep_alive();
        intent = new Intent(this, keep_alive.getClass());
        if (!isServicerunning(keep_alive.getClass()))
            startService(intent);

        if (!Pushy.isRegistered(getApplicationContext()))
            new RegisterUser(this).execute();
        Pushy.listen(this);
    }


    private boolean isServicerunning(Class<? extends Keep_alive> aClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (aClass.getName().equals(serviceInfo.service.getClassName())) {
                Log.d(TAG, " Service Already Running");
                return true;
            }
            Log.d(TAG, " Service Not Running");
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        Intent intent = new Intent();
        intent.setAction("restartservice");
        intent.setClass(this, PushReceiver.class);
        intent.putExtra("O", "1");
        this.sendBroadcast(intent);
       // aboolean = true;
        super.onDestroy();
    }



    //Step 1
    private boolean checkMapServices() {
        if (isServicesOK())
            if (isMapsEnabled())
                return true;
        return false;
    }


    //Step 2
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            new utils().message(" An error occurred but we can fix it pls follow the instruction", getApplicationContext());
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else
            new utils().message("You can't make map requests", getApplicationContext());

        return false;
    }


    //Step 3
    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        } else {
            Log.d(TAG, " Added Location Permission");
            getUserDetails();
            return true;
        }
    }

    //Step 4
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    //Step 5
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getUserDetails();
                    Log.d(TAG, " Added Location Permission");
                } else
                    Log.d(TAG, "Location Declined");


            }
        }

    }


    //Step 5b
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    //Step 6
    public void POLICY_SERVICE() {
        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Log.d(TAG, " Called ! ");
        }
    }


    //Step 7
    protected void CHECK_FOR_PERMISSION() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("OK");
        } else
            request_permission();
    }


    //Step 8
    private void request_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This Permission is needed for file sharing")
                    .setPositiveButton("ok", (dialogInterface, i) -> ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE)).setNegativeButton("cancel",
                    (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(this), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);

        }
    }


    //Step 9
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                new utils().message2("Permission Granted", this);
            else
                new utils().message2("Permission Denied", this);

        }

    }


    //Step 2
    private void getUserDetails() {
        if (muserLocation == null) {
            DocumentReference user_ref = FirebaseFirestore.getInstance().collection(getString(R.string.DELIVERY_REG)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
            user_ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, " onComplete successfully got details");
                        task1 = task;
                        getLast_know_Location(task1);
                    }
                }
            });
        } else
            getLast_know_Location(task1);
    }


    //Step 3
    private void getLast_know_Location(Task<DocumentSnapshot> task) {
        Log.d(TAG, " requesting for last known location");
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
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        User user = task.getResult().toObject(User.class);
                        muserLocation = new UserLocation(geoPoint, null, user);
                        if (FirebaseAuth.getInstance().getUid() != null && geoPoint.getLatitude()>0 && geoPoint.getLongitude()>0)
                            saveUserLocation(geoPoint.getLatitude(),geoPoint.getLongitude());

                    }
                }
            }
        };
        LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void saveUserLocation(double latitude, double longitude) {
        Log.d(TAG, "saveUserLocation: "+latitude+"   "+longitude);
    }
}