package com.kaiann.fypdealsmatchapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaiann.fypdealsmatchapp.Interface.ItemClickListener;
import com.kaiann.fypdealsmatchapp.Model.Complete;
import com.kaiann.fypdealsmatchapp.ViewHolder.CompleteViewHolder;

import java.lang.reflect.Array;


public class DealComplete extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager manager;

    FirebaseRecyclerAdapter<Complete, CompleteViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference complete;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_complete);

        database = FirebaseDatabase.getInstance();
        complete = database.getReference("Complete");
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.listComplete);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        final String user = auth.getCurrentUser().getUid();

        loadOrders(user);

    }

    private void loadOrders(final String user) {

        adapter = new FirebaseRecyclerAdapter<Complete, CompleteViewHolder>(
                Complete.class,
                R.layout.complete_layout,
                CompleteViewHolder.class,
                complete.child(user)
        ) {
            @Override
            protected void populateViewHolder(final CompleteViewHolder viewHolder, Complete model, final int position) {

                //fill both partner names
                final String itemId = adapter.getRef(position).getKey();
                final DatabaseReference partnerId = complete.child(user).child(itemId);
                partnerId.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String partnerId;
                        partnerId = dataSnapshot.child("partner").getValue(String.class);


                        DatabaseReference matchRefs = database.getReference("Partner").child(partnerId);
                        matchRefs.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String user1, user2;
                                user1 = dataSnapshot.child("user1").getValue(String.class);
                                user2 = dataSnapshot.child("user2").getValue(String.class);

                                DatabaseReference username = database.getReference("Users");
                                username.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final String username1, username2;
                                        username1 = dataSnapshot.child(user1).child("name").getValue(String.class);
                                        username2 = dataSnapshot.child(user2).child("name").getValue(String.class);

                                        viewHolder.txtPartner.setText(username1);
                                        viewHolder.txtPartner2.setText(username2);

                                        viewHolder.setItemClickListener(new ItemClickListener() {
                                            @Override
                                            public void onClick(View view, int position, boolean isLongClick) {
                                                //add intent to chat page
                                                Intent chat = new Intent(DealComplete.this, ChatActivity.class);
                                                chat.putExtra("partner_id", partnerId);
                                                chat.putExtra("user1", user1);
                                                chat.putExtra("user2", user2);
                                                startActivity(chat);

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //fill item name
                DatabaseReference itemRef = database.getReference("Item").child(itemId);
                itemRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String itemName;
                        itemName = dataSnapshot.child("name").getValue(String.class);
                        viewHolder.txtCDealName.setText(itemName);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        };
        recyclerView.setAdapter(adapter);
    }

}
