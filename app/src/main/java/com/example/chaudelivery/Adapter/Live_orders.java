package com.example.chaudelivery.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaudelivery.R;
import com.example.chaudelivery.Running_Service.Pusher;
import com.example.chaudelivery.UI.Accept_Order;
import com.example.chaudelivery.UI.Map_views;
import com.example.chaudelivery.model.User;
import com.example.chaudelivery.utils.Constant;
import com.example.chaudelivery.utils.UserLocation;
import com.example.chaudelivery.utils.utils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Live_orders extends RecyclerView.Adapter<Live_orders.MyHolder> {


    private Context context;
    private List<Map<String, Object>> pack;
    private List<UserLocation> points = new ArrayList<>();
    private Map<String, Object> pay_load;

    private User user1;
    private String TAG = "Live_orders";


    public Live_orders(Context context, List<Map<String, Object>> pack) {
        this.context = context;
        this.pack = pack;
    }

    @NonNull
    @Override
    public Live_orders.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opened_orders, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            user1 = new Accept_Order().call(holder.completed_order.getContext());

        Populate(holder, position);
        holder.order_location.setOnClickListener(u -> {
            holder.progressBar2.setVisibility(View.VISIBLE);
            points.add(Docs(pack.get(position).get("Pick_up_geo_point").toString(), "Pickup", u, pack.get(position).get("Vendor").toString(),pack.get(position).get("Vendor_img_url").toString()));
            points.add(Docs(pack.get(position).get("Drop_off_geo_point").toString(), "Dropoff", u, pack.get(position).get("Client_name").toString(),pack.get(position).get("user_img_url").toString()));
            if (points.size() == 2) {
                u.getContext().startActivity(new Intent(u.getContext(), Map_views.class).putExtra("GEO_POINTS", new Gson().toJson(points)).putExtra("total",pack.get(position).get("Total").toString()));
                Constant.VENDOR_NO = pack.get(position).get("Vendor_Phone").toString();
                }
            });


        holder.completed_order.setOnClickListener(y -> {
            holder.progressBar2.setVisibility(View.VISIBLE);
            SEND_NOTIFICATION(pack.get(position).get("Client_ID"), y, holder, position);
        });


        holder.dialer.setOnClickListener(u -> {
            start_dialer(pack.get(position).get("Drop_off_phone_no").toString(), holder.dialer.getContext());
        });
    }


    public void start_dialer(String number, Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: " + number));
        context.startActivity(intent);
    }


    private UserLocation  Docs(String drop, String user, View v, String name, String img) {
        return new UserLocation(new GeoPoint(Double.parseDouble(drop.substring(drop.indexOf("=") + 1, drop.indexOf(","))), Double.parseDouble(drop.substring(drop.lastIndexOf("=") + 1, drop.length() - 1))), null, new utils().SERVE(user, name,img));
    }


    @Override
    public int getItemCount() {
        return pack.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        private TextView drop_off_phone, order_item_id, time_stamp, vendor_name, vendor_phone, order_id, vendor_id, Vendor_business_D;
        private CircleImageView vendor_img;
        private ProgressBar progressBar, progressBar2;
        private CardView order_location;
        private Button completed_order, dialer;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            drop_off_phone = itemView.findViewById(R.id.drop_off_phone);
            order_item_id = itemView.findViewById(R.id.order_item_id);
            time_stamp = itemView.findViewById(R.id.time_stamp);
            vendor_name = itemView.findViewById(R.id.vendor_name);
            vendor_phone = itemView.findViewById(R.id.vendor_phone);
            Vendor_business_D = itemView.findViewById(R.id.Vendor_business_D);
            order_id = itemView.findViewById(R.id.order_id);
            vendor_id = itemView.findViewById(R.id.vendor_id);
            progressBar = itemView.findViewById(R.id.progressBar);
            progressBar2 = itemView.findViewById(R.id.progressBar2);
            vendor_img = itemView.findViewById(R.id.vendor_img);
            order_location = itemView.findViewById(R.id.order_location);
            completed_order = itemView.findViewById(R.id.completed_order);
            dialer = itemView.findViewById(R.id.call);
        }
    }


    private void SEND_NOTIFICATION(Object client_id, View view, MyHolder holder, int position) {

        FirebaseFirestore.getInstance().collection(view.getContext().getString(R.string.USER_REG)).document(client_id.toString()).get()
                .addOnCompleteListener(u -> {
                    if (u.isSuccessful()) {
                        User user = u.getResult().toObject(User.class);
                        if (user.getToken().length() > 0) {
                            pay_load = new HashMap<>();
                            pay_load.put("notification", "Order Arrived");
                            pay_load.put("By", user.getUsername());
                            pay_load.put("from", user1.getUsername());
                            pay_load.put("img_url", user1.getImg_url());
                            pay_load.put("Vendor_ID", pack.get(position).get("Vendor_ID"));
                            pay_load.put("Client_ID", pack.get(position).get("Client_ID"));
                            pay_load.put("doc_id_Gen", pack.get(position).get("doc_id_Gen"));
                            Pusher.pushrequest push = new Pusher.pushrequest(pay_load, user.getToken());
                            try {
                                Pusher.sendPush(push);
                                new utils().message("Notification sent to " + user.getUsername(), view.getContext());
                                REMOVE(holder.completed_order.getContext());
                                holder.progressBar2.setVisibility(View.INVISIBLE);
                            } catch (Exception ex) {
                                Log.d(TAG, "SEND_NOTIFICATION: " + ex.toString());
                            }
                        } else {
                            new utils().message("User: " + user.getUsername() + " is offline ", view.getContext());
                            holder.progressBar2.setVisibility(View.INVISIBLE);
                        }
                    } else
                        new utils().message("Error occurred " + u.getException(), view.getContext());
                });


    }

    private void REMOVE(Context context) {
        new utils().APPLY(context, Integer.parseInt(new utils().init(context.getApplicationContext()).getString(context.getString(R.string.OPENED_ORDERS_COUNT), null)) - 1);

    }


    private void Populate(MyHolder holder, int position) {
        holder.vendor_id.setText(" " + pack.get(position).get("Vendor_ID").toString());
        holder.order_id.setText("  " + pack.get(position).get("Order_id").toString());
        holder.vendor_phone.setText(" vendor no: " + pack.get(position).get("Vendor_Phone").toString());
        holder.vendor_name.setText(" " + pack.get(position).get("Vendor").toString());
        holder.time_stamp.setText(" " + pack.get(position).get("Timestamp").toString());
        String x = (Integer.parseInt(pack.get(position).get("Order_items").toString()) > 1) ? "packages" : "package";
        holder.order_item_id.setText(" " + pack.get(position).get("Order_items").toString() + " " + x);
        holder.drop_off_phone.setText(" client no: " + pack.get(position).get("Drop_off_phone_no").toString());
        holder.Vendor_business_D.setText(" " + pack.get(position).get("Vendor_business_D").toString());
        new utils().IMG(holder.vendor_img, Constant.IMG_URL + pack.get(position).get("Vendor_img_url").toString(), holder.progressBar);

    }
}
