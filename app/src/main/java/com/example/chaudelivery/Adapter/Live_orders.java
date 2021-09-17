package com.example.chaudelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaudelivery.R;
import com.example.chaudelivery.utils.Constant;
import com.example.chaudelivery.utils.utils;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Live_orders extends RecyclerView.Adapter<Live_orders.MyHolder> {


    private Context context;
    private List<Map<String, Object>> pack;


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
    public void onBindViewHolder(@NonNull Live_orders.MyHolder holder, int position) {

        Populate(holder,position);
        holder.order_location.setOnClickListener(u -> {

        });
    }



    @Override
    public int getItemCount() {
        return pack.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        private TextView drop_off_phone, order_item_id, time_stamp, vendor_name, vendor_phone, order_id, vendor_id, Vendor_business_D;
        private CircleImageView vendor_img;
        private ProgressBar progressBar;
        private CardView order_location;

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
            vendor_img = itemView.findViewById(R.id.vendor_img);
            order_location = itemView.findViewById(R.id.order_location);

        }
    }



    private void Populate(MyHolder holder, int position) {
        holder.vendor_id.setText(" " + pack.get(position).get("Vendor_ID").toString());
        holder.order_id.setText(" " + pack.get(position).get("Order_id").toString());
        holder.vendor_phone.setText(" " + pack.get(position).get("Vendor_Phone").toString());
        holder.vendor_name.setText(" " + pack.get(position).get("Vendor").toString());
        holder.time_stamp.setText(" " + pack.get(position).get("Timestamp").toString());
        String x = (Integer.parseInt(pack.get(position).get("Order_items").toString()) > 1) ? "packages" : "package";
        holder.order_item_id.setText(" " + pack.get(position).get("Order_items").toString() + " " + x);
        holder.drop_off_phone.setText(" " + pack.get(position).get("Drop_off_phone_no").toString());
        holder.Vendor_business_D.setText(" " + pack.get(position).get("Vendor_business_D").toString());
        new utils().IMG(holder.vendor_img, Constant.IMG_URL + pack.get(position).get("Vendor_img_url").toString(), holder.progressBar);

    }
}
