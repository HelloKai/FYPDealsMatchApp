package com.kaiann.fypdealsmatchapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaiann.fypdealsmatchapp.Interface.ItemClickListener;
import com.kaiann.fypdealsmatchapp.Model.Request;
import com.kaiann.fypdealsmatchapp.ViewHolder.StatusViewHolder;

public class DealStatus extends AppCompatActivity {

    public RecyclerView recyclerView;

    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference request;
    FirebaseAuth auth;

    FirebaseRecyclerAdapter<Request, StatusViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_status);

        database = FirebaseDatabase.getInstance();
        request = database.getReference("Request");
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.listDeals);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final String user = auth.getCurrentUser().getUid();

        loadRequests(user);
    }

//    loadRequest take in userid, convert itemid into item_name before displaying

    private void loadRequests(String user) {

        adapter = new FirebaseRecyclerAdapter<Request, StatusViewHolder>(
                Request.class, R.layout.deal_layout, StatusViewHolder.class,
                request.orderByChild("uid").equalTo(user)
        ) {
            @Override
            protected void populateViewHolder(final StatusViewHolder viewHolder, Request model, int position) {

                String itemId = adapter.getRef(position).getKey();
                DatabaseReference itemRef = database.getReference("Item").child(itemId);
                itemRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name;
                        name = dataSnapshot.child("name").getValue(String.class);
                        viewHolder.txtDealName.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.txtDealStatus.setText("Request pending");

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(DealStatus.this, "Finding you a match...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

    }
}
