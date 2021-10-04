package com.example.chaudelivery.Running_Service;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.chaudelivery.R;
import com.example.chaudelivery.utils.utils;

import java.net.URL;

import me.pushy.sdk.Pushy;

public class RegisterUser extends AsyncTask<Void,Void,Object> {

    private Activity activity;
    private String TAG ="RegisterUser";

    public RegisterUser(Activity context) {
        this.activity = context;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        try {
            String deviceToken = Pushy.register(activity.getApplicationContext());
            Log.d(TAG,"Pushy Device token: "+deviceToken);

            new URL("https://com.example.chauvendor/regsiter/device?token="+deviceToken).openConnection();

            return  deviceToken;
        } catch (Exception e) {

            return e;
        }
    }


    @Override
    protected void onPostExecute(Object o) {

       if(o instanceof Exception){
           Log.d(TAG,  o.toString());
           new utils().init(activity.getApplicationContext())
                   .edit().putBoolean(activity.getString(R.string.DEVICE_REG_TOKEN),false).apply();

           new utils().init(activity.getApplicationContext())
                   .edit().putString(activity.getString(R.string.DEVICE_TOKEN),"").apply();
       }
       else{
           new utils().init(activity.getApplicationContext())
                   .edit().putBoolean(activity.getString(R.string.DEVICE_REG_TOKEN),true).apply();

           new utils().init(activity.getApplicationContext())
                   .edit().putString(activity.getString(R.string.DEVICE_TOKEN),String.valueOf(o)).apply();
       }


    }
}
