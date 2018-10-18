package com.kaiann.fypdealsmatchapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaiann.fypdealsmatchapp.Interface.ItemClickListener;
import com.kaiann.fypdealsmatchapp.Model.Item;
import com.kaiann.fypdealsmatchapp.ViewHolder.DealViewHolder;
import com.squareup.picasso.Picasso;

public class ItemList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference itemList;

    String categoryId="";

    FirebaseRecyclerAdapter<Item, DealViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        database = FirebaseDatabase.getInstance();
        itemList = database.getReference("Item");

        recyclerView = findViewById(R.id.recycler_item);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //get intent from prev page
        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId != null)
           loadListItem(categoryId);
//check if categoryId is passed
        Toast.makeText(ItemList.this, ""+categoryId, Toast.LENGTH_SHORT).show();
    }

    private void loadListItem(String categoryId) {




        adapter = new FirebaseRecyclerAdapter<Item, DealViewHolder>
                (Item.class, R.layout.deal_item, DealViewHolder.class, itemList.orderByChild("MenuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(DealViewHolder viewHolder, Item model, int position) {
                viewHolder.deal_name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.deal_image);

                final Item local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(ItemList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
//                        Intent itemDetail = new Intent(DealsHome.this, DealItem.class);
//                        itemDetail.putExtra("ItemId", adapter.getRef(position).getKey()); //send item id to new activity
//                        startActivity(itemDetail);
                    }
                });

                recyclerView.setAdapter(adapter);
            }
        };

    }
}
