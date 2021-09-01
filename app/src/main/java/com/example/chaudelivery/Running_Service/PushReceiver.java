package com.example.chaudelivery.Running_Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.chaudelivery.R;
import com.example.chaudelivery.UI.MainActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import me.pushy.sdk.Pushy;

import static com.example.chaudelivery.utils.Constant.NOTIFICATION_TITLE;
import static com.example.chaudelivery.utils.Constant.*;
import static com.example.chaudelivery.utils.Constant.NOTIFICATION_TITLE;

public class PushReceiver extends BroadcastReceiver {

    private String TAG = "PushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationText = "";
        if (intent.getStringExtra("user") != null)
            notificationText = intent.getStringExtra("user");

        int notificationID = new Random().nextInt(2000);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Prepare a notification with vibration, sound and lights
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notify)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(notificationText)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).putExtra("ID", (intent.getStringExtra("ID") != null) ? intent.getStringExtra("ID") : ""), PendingIntent.FLAG_ONE_SHOT));

        if (intent.getStringExtra("img_url") != null) {
            Bitmap bitmap = get_img_url(intent.getStringExtra("img_url"));
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)).setLargeIcon(bitmap);
            Log.d(TAG, intent.getStringExtra("img_url"));
        }

        Pushy.setNotificationChannel(builder, context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, builder.build());
        if (intent.getExtras() != null)
            if (intent.getStringExtra("O").equals("1"))
                notificationManager.cancelAll();
    }


    private Bitmap get_img_url(String img_url) {
        try {
            URL url = new URL(img_url);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
