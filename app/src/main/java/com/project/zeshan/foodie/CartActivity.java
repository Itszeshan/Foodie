package com.project.zeshan.foodie;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.project.zeshan.foodie.Common.Common;
import com.project.zeshan.foodie.Common.SwipeToDeleteCallback;
import com.project.zeshan.foodie.Database.Database;
import com.project.zeshan.foodie.Model.Order;
import com.project.zeshan.foodie.Model.Request;
import com.project.zeshan.foodie.ViewHolder.CartAdapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    static TextView txtTotalPrice;
    Button btnConfirm;

    static List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    static CardView check_out;
    static FrameLayout noItem;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/of.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_cart);

        check_out = (CardView) findViewById(R.id.check_out);
        noItem = (FrameLayout) findViewById(R.id.no_item);

        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnConfirm = (Button) findViewById(R.id.btnPlaceOrder);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });


        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address:");

        final EditText editAddress = new EditText(CartActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        editAddress.setLayoutParams(lp);

        alertDialog.setView(editAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        editAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );

                FirebaseDatabase.getInstance().getReference().child("requests")
                        .child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);
                new Database(getBaseContext()).cleanCart();

                Toast.makeText(CartActivity.this, "Thank You! your order has been placed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    public static void deleteWithSwipe(int position, Context context)
    {
        cart.remove(position);
        if(cart.size() <= 0)
        {
            check_out.setVisibility(View.GONE);
            noItem.setVisibility(View.VISIBLE);
        }

        new Database(context).cleanCart();

        for(Order item: cart)
        {
            new Database(context).addToCart(item);
        }

        int total = 0;

        for(Order order: cart)
        {
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }

        txtTotalPrice.setText(total + " PKR");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
        {
            deleteCart(item.getOrder());
            loadListFood();
        }
        return true;
    }

    private void deleteCart(int position) {
        cart.remove(position);

        new Database(CartActivity.this).cleanCart();

        for(Order item: cart)
        {
            new Database(this).addToCart(item);
        }
    }

    private void loadListFood() {
        cart = new Database(this).getCarts();
        if(cart.size() <= 0)
        {
            check_out.setVisibility(View.GONE);
            noItem.setVisibility(View.VISIBLE);
        }

        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        int total = 0;

        for(Order order: cart)
        {
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }

        txtTotalPrice.setText(total + " PKR");
    }

}
