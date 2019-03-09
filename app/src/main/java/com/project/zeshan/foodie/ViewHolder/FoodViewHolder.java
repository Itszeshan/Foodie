package com.project.zeshan.foodie.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.zeshan.foodie.Interface.ItemClickListener;
import com.project.zeshan.foodie.R;


public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name, food_price;
    public ImageView food_image, fav_img, share_img, cart_image;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_image = (ImageView) itemView.findViewById(R.id.food_image);
        food_name = (TextView) itemView.findViewById(R.id.food_name);
        fav_img = (ImageView) itemView.findViewById(R.id.fav);
        share_img = (ImageView) itemView.findViewById(R.id.btnShare);
        food_price = (TextView) itemView.findViewById(R.id.food_price);
        cart_image = (ImageView) itemView.findViewById(R.id.cart_button_card);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
