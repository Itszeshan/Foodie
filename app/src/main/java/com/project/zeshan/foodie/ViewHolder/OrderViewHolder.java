package com.project.zeshan.foodie.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project.zeshan.foodie.Interface.ItemClickListener;
import com.project.zeshan.foodie.R;

import org.w3c.dom.Text;


public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId;
    public TextView txtOrderPhone;
    public TextView txtOrderStatus;
    public TextView txtOrderAddress;
    ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
