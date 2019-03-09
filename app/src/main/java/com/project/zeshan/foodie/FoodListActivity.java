package com.project.zeshan.foodie;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;
import com.project.zeshan.foodie.Common.Common;
import com.project.zeshan.foodie.Database.Database;
import com.project.zeshan.foodie.Interface.ItemClickListener;
import com.project.zeshan.foodie.Model.Food;
import com.project.zeshan.foodie.Model.Order;
import com.project.zeshan.foodie.ViewHolder.FoodViewHolder;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodListActivity extends AppCompatActivity {

    RecyclerView recycler_food;
    RecyclerView.LayoutManager layoutManager;
    String categoryId;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //For searching
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestionList = new ArrayList<>();
    MaterialSearchBar searchBar;
    Database localDB;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    SwipeRefreshLayout swipeRefreshLayout;
    FrameLayout noFoodItem;

    SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(resource)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if(searchAdapter != null)
        searchAdapter.stopListening();
    }

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
        setContentView(R.layout.activity_food_list);

        recycler_food = (RecyclerView) findViewById(R.id.recycler_food);
        recycler_food.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);
        localDB = new Database(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_food_list);

        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );


        noFoodItem = (FrameLayout) findViewById(R.id.no_food);

        if(getIntent() != null)
        {
            categoryId = getIntent().getStringExtra("categoryId");
        }

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(!categoryId.isEmpty() && categoryId !=null)
                        {
                            if(Common.isConnectedToInternet(getBaseContext()))
                                loadListFood(categoryId);
                            else
                                Toast.makeText(getBaseContext(), "Please check your connection..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(!categoryId.isEmpty() && categoryId !=null)
                {
                    if(Common.isConnectedToInternet(getBaseContext()))
                        loadListFood(categoryId);
                    else
                        Toast.makeText(getBaseContext(), "Please check your connection..", Toast.LENGTH_SHORT).show();
                }
            }
        });





        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        searchBar.setHint("Enter food");
        loadSuggestions();
        searchBar.setLastSuggestions(suggestionList);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for(String search: suggestionList)
                {
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                    {
                        suggest.add(search);
                    }
                }

                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                {
                    recycler_food.setAdapter(adapter);
                    //searchAdapter.stopListening();
                }

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                searchFood(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                super.onButtonClicked(buttonCode);
            }
        });
    }

    private void searchFood(CharSequence text) {
        Log.d("DL1", text.toString());
        Query query = FirebaseDatabase.getInstance().getReference().child("foods").orderByChild("name").equalTo(text.toString());
        Log.d("DL2", query.toString());

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query, new SnapshotParser<Food>() {
                    @NonNull
                    @Override
                    public Food parseSnapshot(@NonNull DataSnapshot snapshot) {
                       Log.d("DL3", "NO");
                        String name = null,image = null,price = null,desc = null,discount = null,menuId = null;
                        if(snapshot.exists())
                        {
                            name = snapshot.child("name").getValue().toString();
                            image = snapshot.child("image").getValue().toString();
                            price = snapshot.child("price").getValue().toString();
                            desc = snapshot.child("desc").getValue().toString();
                            discount = snapshot.child("discount").getValue().toString();
                            menuId = snapshot.child("menuId").getValue().toString();
                        }

                        Log.d("DL2", name);

                        return new Food(name, image, price, desc, discount, menuId);
                    }
                }).build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.food_name.setText(model.getName());
                Glide.with(getBaseContext()).load(model.getImage()).into(holder.food_image);

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(FoodListActivity.this, FoodDetailActivity.class);
                        intent.putExtra("foodId", searchAdapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_item, viewGroup, false);
                return new FoodViewHolder(view);
            }
        };

        searchAdapter.startListening();
        recycler_food.setAdapter(searchAdapter);
    }

    private void loadSuggestions() {

         FirebaseDatabase.getInstance().getReference().child("foods").orderByChild("menuId")
                .equalTo(categoryId).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists())
                 {
                     for(DataSnapshot snapshot: dataSnapshot.getChildren())
                     {
                         suggestionList.add(snapshot.child("name").getValue().toString());
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

    }

    private void loadListFood(String categoryId) {
        final boolean[] checkEmpty = {true};
        Query query = FirebaseDatabase.getInstance().getReference().child("foods").orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query, new SnapshotParser<Food>() {
                    @NonNull
                    @Override
                    public Food parseSnapshot(@NonNull DataSnapshot snapshot) {
                        String name = null,image = null,price = null,desc = null,discount = null,menuId = null;
                        if(snapshot.exists())
                        {
                            checkEmpty[0] = false;
                            name = snapshot.child("name").getValue().toString();
                            image = snapshot.child("image").getValue().toString();
                            price = snapshot.child("price").getValue().toString();
                            desc = snapshot.child("desc").getValue().toString();
                            discount = snapshot.child("discount").getValue().toString();
                            menuId = snapshot.child("menuId").getValue().toString();
                        }

                        return new Food(name, image, price, desc, discount, menuId);
                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder holder, final int position, @NonNull final Food model) {
                if(!checkEmpty[0])
                {
                    Log.d("LL1", "hann");
                    noFoodItem.setVisibility(View.GONE);
                }
                holder.food_name.setText(model.getName());
                Glide.with(getBaseContext()).load(model.getImage()).into(holder.food_image);
                holder.food_price.setText(model.getPrice() + " PKR");
                if(localDB.isFav(adapter.getRef(position).getKey()))
                    holder.fav_img.setImageResource(R.drawable.ic_favorite_black_24dp);

                holder.fav_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!localDB.isFav(adapter.getRef(position).getKey()))
                        {
                            localDB.addToFav(adapter.getRef(position).getKey());
                            holder.fav_img.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodListActivity.this, "added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeToFav(adapter.getRef(position).getKey());
                            holder.fav_img.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodListActivity.this, "removed from favorites", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                holder.share_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Glide
                                .with(getBaseContext())
                                .asBitmap()
                                .load(model.getImage())
                                .into(target);
                    }
                });

                holder.cart_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Database(getBaseContext()).addToCart(new Order(
                                adapter.getRef(position).getKey(),
                                model.getName(),
                                "1",
                                model.getPrice(),
                                model.getDiscount()
                        ));

                        Toast.makeText(FoodListActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                    }
                });

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(FoodListActivity.this, FoodDetailActivity.class);
                        intent.putExtra("foodId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_item, viewGroup, false);
                return new FoodViewHolder(view);
            }
        };

        adapter.startListening();
        recycler_food.setAdapter(adapter);
        if(checkEmpty[0])
        {
            Log.d("LL1", "hann");
            noFoodItem.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
