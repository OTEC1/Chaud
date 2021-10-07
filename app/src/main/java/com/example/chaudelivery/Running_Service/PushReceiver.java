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
import com.example.chaudelivery.UI.Accept_Order;

import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import me.pushy.sdk.Pushy;

import static com.example.chaudelivery.utils.Constant.NOTIFICATION_TITLE;
import static com.example.chaudelivery.utils.Constant.*;

public class PushReceiver extends BroadcastReceiver {

    private String TAG = "PushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent intent1 = new Intent(context, Accept_Order.class);
        intent1.putExtra("Vendor",intent.getStringExtra("Vendor"));
        intent1.putExtra("Vendor_img_url", intent.getStringExtra("Vendor_img_url"));
        intent1.putExtra("Vendor_ID",intent.getStringExtra("Vendor_ID"));
        intent1.putExtra("Client_ID",intent.getStringExtra("Client_ID"));
        intent1.putExtra("Client_name", intent.getStringExtra("Client_name"));
        intent1.putExtra("Vendor_Phone", intent.getStringExtra("Vendor_Phone"));
        intent1.putExtra("Order_id", intent.getStringExtra("Order_id"));
        intent1.putExtra("Order_items", intent.getIntExtra("Order_items",0));
        intent1.putExtra("Vendor_business_D", intent.getStringExtra("Vendor_business_D"));
        intent1.putExtra("Pick_up_geo_point", intent.getStringExtra("Pick_up_geo_point"));
        intent1.putExtra("Drop_off_geo_point", intent.getStringExtra("Drop_off_geo_point"));
        intent1.putExtra("Drop_off_phone_no", intent.getStringExtra("Drop_off_phone_no"));
        intent1.putExtra("Timestamp", intent.getLongExtra("Timestamp",0));
        intent1.putExtra("doc_id_Gen", intent.getStringExtra("doc_id_Gen"));
        intent1.putExtra("user_img_url", intent.getStringExtra("user_img_url"));
        int notificationID = new Random().nextInt(3000);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notify)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(intent.getStringExtra("Vendor"))
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        if (intent.getStringExtra("Vendor_img_url") != null) {
            Bitmap bitmap = get_img_url(intent.getStringExtra("Vendor_img_url"));
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)).setLargeIcon(bitmap);
        }

        Pushy.setNotificationChannel(builder, context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, builder.build());
        if (intent.getStringExtra("O") != null)
            if (intent.getStringExtra("O").equals("1"))
                notificationManager.cancelAll();
    }


    private Bitmap get_img_url(String img_url) {
        try {
            URL url = new URL(IMG_URL+img_url);
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
