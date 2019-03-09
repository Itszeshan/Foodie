package com.project.zeshan.foodie;

import android.app.DownloadManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project.zeshan.foodie.Common.Common;
import com.project.zeshan.foodie.Model.Request;
import com.project.zeshan.foodie.ViewHolder.OrderViewHolder;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView listOrder;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    SwipeRefreshLayout swipeRefreshLayout;


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
        setContentView(R.layout.activity_order_status);

        listOrder  = (RecyclerView) findViewById(R.id.listOrders);
        listOrder.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listOrder.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_order_status);
        final String phone = Common.currentUser.getPhone();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOrders(phone);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );



        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadOrders(phone);
            }
        });

    }

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
    }

    private void loadOrders(String phone) {
        Query query = FirebaseDatabase.getInstance().getReference().child("requests")
                .orderByChild("phone").equalTo(phone);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(query, new SnapshotParser<Request>() {
                    @NonNull
                    @Override
                    public Request parseSnapshot(@NonNull DataSnapshot snapshot) {
                        String id = null,phone = null,address = null,status = null;
                        if(snapshot.exists())
                        {
                            phone = snapshot.child("phone").getValue().toString();
                            id = snapshot.getKey();
                            address = snapshot.child("address").getValue().toString();
                            status = convertCodeToStatus(snapshot.child("status").getValue().toString());
                        }
                        Request request = new Request(phone, null, address, null,null);
                        request.setId(id);
                        request.setStatus(status);

                        return request;
                    }
                }).build();

        adapter =  new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Request model) {
                holder.txtOrderId.setText(model.getId());
                holder.txtOrderStatus.setText(model.getStatus());
                holder.txtOrderPhone.setText(model.getPhone());
                holder.txtOrderAddress.setText(model.getAddress());
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_layout, viewGroup, false);
                return new OrderViewHolder(view);
            }
        };

        adapter.startListening();
        listOrder.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private String convertCodeToStatus(String status) {
        switch(status)
        {
            case "0":
                return "ORDER PLACED";
            case "1":
                return "ORDER PICKED";
            case "2":
                return "ORDER DELIVERED";
        }

        return null;
    }
}
