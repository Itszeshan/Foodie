package com.project.zeshan.foodie.Common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.project.zeshan.foodie.CartActivity;
import com.project.zeshan.foodie.Database.Database;
import com.project.zeshan.foodie.Model.Order;
import com.project.zeshan.foodie.R;
import com.project.zeshan.foodie.ViewHolder.CartAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private CartAdapter adapter;
    private Drawable icon;
    private final ColorDrawable background;

    public SwipeToDeleteCallback(CartAdapter cartAdapter) {
        super(0, ItemTouchHelper.LEFT);
        adapter = cartAdapter;
        background =  new ColorDrawable(Color.RED);
        icon = ContextCompat.getDrawable(adapter.context, R.drawable.ic_delete_black_24dp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            icon.setTint(Color.WHITE);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight())/2;
        int iconTop = itemView.getTop() + iconMargin;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if(dX < 0)
        {
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int)dX) - backgroundCornerOffset, itemView.getTop(),
                    itemView.getRight(),itemView.getBottom());
        }
        else
        {
            background.setBounds(0,0,0,0);
        }

        background.draw(c);
        icon.draw(c);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();

        CartActivity.deleteWithSwipe(position,adapter.context);
        adapter.notifyDataSetChanged();

    }
}
