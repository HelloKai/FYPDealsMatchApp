package com.kaiann.fypdealsmatchapp;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaiann.fypdealsmatchapp.Model.Complete;
import com.kaiann.fypdealsmatchapp.Model.Item;
import com.kaiann.fypdealsmatchapp.Model.Request;
import com.squareup.picasso.Picasso;

public class DealItem extends AppCompatActivity {

    private TextView item_name, item_location, item_description;
    private ImageView item_image;
    CollapsingToolbarLayout collapsingToolbarLayout;

    //change to btnfav
    FloatingActionButton btnCart;

    String itemId="";

    FirebaseDatabase database;
    DatabaseReference item;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_item);

        database = FirebaseDatabase.getInstance();
        item = database.getReference("Item");
        auth = FirebaseAuth.getInstance();

        btnCart = findViewById(R.id.btnCart);

        item_description = findViewById(R.id.item_description);
        item_name = findViewById(R.id.item_name);
        item_location = findViewById(R.id.item_location);
        item_image = findViewById(R.id.img_item);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);


        if(getIntent() !=null){
            itemId =getIntent().getStringExtra("ItemId");
        }
        if(!itemId.isEmpty()){
            getDetailItem(itemId);
        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatabaseReference reqRef = database.getReference("Request").child(itemId);

                reqRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String user = auth.getCurrentUser().getUid();
                        DatabaseReference userRef = database.getReference("Users").child(user);

                        if(!dataSnapshot.hasChildren()){
                            //else add current user into request db

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String name, phone;
                                    name = dataSnapshot.child("name").getValue(String.class);
                                    phone = dataSnapshot.child("phone").getValue(String.class);

                                    //create new request
                                    Request newRequest = new Request();
                                    newRequest.setUid(user);
                                    newRequest.setName(name);
                                    newRequest.setPhone(phone);

                                    //save to request db
                                    database.getReference("Request").child(itemId).setValue(newRequest);

                                    Toast.makeText(DealItem.this, "REQUEST MADE!", Toast.LENGTH_SHORT).show();

                                    //ADD ORDER CARD PENDING REQUEST CODE BELOW

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }


                            });

                            return;

                        }else{
                            //if 1 user exists, get user name and phone

                            String name1, phone1, uidcheck;
                            name1 = dataSnapshot.child("name").getValue(String.class);
                            phone1 = dataSnapshot.child("phone").getValue(String.class);
                            uidcheck = dataSnapshot.child("uid").getValue(String.class);

                            //only display text if current uid and uid in db is different
                            if(!uidcheck.equals(user)){
                                //match found please check your order cart!
                                Toast.makeText(DealItem.this, "Match found: "+name1 +", "+phone1,
                                        Toast.LENGTH_SHORT).show();

                                //move data to Completed db before removing from Request db

                                Complete newComplete1 = new Complete();
                                newComplete1.setName(name1);
                                newComplete1.setPhone(phone1);

                                database.getReference("Complete").child(itemId).child(uidcheck).setValue(newComplete1);

                                //add current user under same Completed itemId db

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String name, phone;
                                        name = dataSnapshot.child("name").getValue(String.class);
                                        phone = dataSnapshot.child("phone").getValue(String.class);

                                        Complete newComplete2 = new Complete();
                                        newComplete2.setName(name);
                                        newComplete2.setPhone(phone);

                                        DatabaseReference existing = database.getReference("Complete").child(itemId);

                                        existing.child(user).setValue(newComplete2);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                //ADD ORDER CART UPDATE MATCH FOUND CODE BELOW


                                //remove matched customer from Request db
                                if(dataSnapshot.getValue() != null) {
                                    database.getReference("Request").child(itemId).removeValue();
                                    return;
                                }


                                return;

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });



            }
        });
    }

    private void getDetailItem(String itemId) {
        item.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Item item = dataSnapshot.getValue(Item.class);

                //fetch Image
                Picasso.get().load(item.getImage()).into(item_image);

                collapsingToolbarLayout.setTitle(item.getName());

                item_location.setText(item.getLocation());

                item_name.setText(item.getName());

                item_description.setText(item.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
