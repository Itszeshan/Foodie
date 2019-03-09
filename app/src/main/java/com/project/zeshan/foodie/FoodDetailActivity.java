package com.project.zeshan.foodie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.zeshan.foodie.Common.Common;
import com.project.zeshan.foodie.Database.Database;
import com.project.zeshan.foodie.Model.Food;
import com.project.zeshan.foodie.Model.Order;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetailActivity extends AppCompatActivity {

    TextView food_name, food_price, food_desc;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String foodId = "";

    Food currentFood;

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
        setContentView(R.layout.activity_food_detail);

        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);

        food_desc = (TextView) findViewById(R.id.food_desc);
        food_name = (TextView) findViewById(R.id.food_name);
        food_price = (TextView) findViewById(R.id.food_price);

        food_image = (ImageView) findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent() != null)
            foodId = getIntent().getStringExtra("foodId");

        if(!foodId.isEmpty())
        {
            if(Common.isConnectedToInternet(getBaseContext()))
                getDetailFood(foodId);
            else
                Toast.makeText(this, "Please check your connection..", Toast.LENGTH_SHORT).show();
        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));

                Toast.makeText(FoodDetailActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getDetailFood(String foodId) {
        DatabaseReference foodDb = FirebaseDatabase.getInstance().getReference()
                .child("foods")
                .child(foodId);

        foodDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = null,image = null,price = null,desc = null,discount = null,menuId = null;
                if(dataSnapshot.exists())
                {
                    name = dataSnapshot.child("name").getValue().toString();
                    image = dataSnapshot.child("image").getValue().toString();
                    price = dataSnapshot.child("price").getValue().toString();
                    desc = dataSnapshot.child("desc").getValue().toString();
                    discount = dataSnapshot.child("discount").getValue().toString();
                    menuId = dataSnapshot.child("menuId").getValue().toString();
                }

                currentFood = new Food(name,image,price,desc,discount,menuId);

                Glide.with(getBaseContext()).load(image).into(food_image);
                collapsingToolbarLayout.setTitle(name);
                food_name.setText(name);
                food_price.setText(price);
                food_desc.setText(desc);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
