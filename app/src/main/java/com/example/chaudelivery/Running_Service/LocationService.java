package com.example.chaudelivery.Running_Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.chaudelivery.R;
import com.example.chaudelivery.model.User;
import com.example.chaudelivery.model.UserLocation;
import com.example.chaudelivery.utils.UserClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

import static com.example.chaudelivery.utils.Constant.*;
import static com.example.chaudelivery.utils.Constant.FAST_INTERVAL;
import static com.example.chaudelivery.utils.Constant.UPDATE_INTERVAL;

public class LocationService extends Service {


    private static final String TAG = "LocationSerivces";
    private FusedLocationProviderClient mfusedLocationProviderClient;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "my channel", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("").setContentText("").build();
            startForeground(1, notification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Notification Started ! ");
        getLocation();
        return START_NOT_STICKY;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getLocation() {

        LocationRequest mlocationRequest = new LocationRequest();
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FAST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Notification Stopped ! ");
            stopSelf();
            return;
        }

        Log.d(TAG, "Service Notification Granted ! ");
        mfusedLocationProviderClient.requestLocationUpdates(mlocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                Log.d(TAG, "onLocationResult requested Valued Ok");

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    User user = ((UserClient) (getApplicationContext())).getUser();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    UserLocation userLocation = new UserLocation(geoPoint,null, user);
                    save_user_location(userLocation);
                }
            }
        }, Objects.requireNonNull(Looper.myLooper()));


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void save_user_location(final UserLocation userLocation) {
        try {
            DocumentReference location = FirebaseFirestore.getInstance().collection(getString(R.string.delivery_location))
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
            location.set(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Log.d(TAG, "onComplete: inserted Vendor location into Fire_store.");
                }
            });

        } catch (NullPointerException e) {
            Log.e(TAG, "saveUserLocation: User instance is null, stopping location service.");
            Log.e(TAG, "saveUserLocation: NullPointerException: " + e.getMessage());
            stopSelf();
        }

    }

}
