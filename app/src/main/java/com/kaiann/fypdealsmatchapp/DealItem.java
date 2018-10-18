package com.kaiann.fypdealsmatchapp;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaiann.fypdealsmatchapp.Model.Item;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_item);

        database = FirebaseDatabase.getInstance();
        item = database.getReference("Item");

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
