package com.kaiann.fypdealsmatchapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaiann.fypdealsmatchapp.Interface.ItemClickListener;
import com.kaiann.fypdealsmatchapp.Model.Category;
import com.kaiann.fypdealsmatchapp.Model.Item;
import com.kaiann.fypdealsmatchapp.ViewHolder.DealViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class ItemList extends AppCompatActivity {

    RecyclerView recycler_item;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLayout;
    FloatingActionButton fab;

    FirebaseDatabase database;
    DatabaseReference itemList;

    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId="";

    FirebaseRecyclerAdapter<Item, DealViewHolder> adapter;

    MaterialEditText edtName, edtDescription, edtLocation;
    FButton btnSelect, btnUpload;

    Item newItem;

    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        //connect to firebase

        database = FirebaseDatabase.getInstance();
        itemList = database.getReference("Item");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recycler_item = findViewById(R.id.recycler_item);
        recycler_item.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_item.setLayoutManager(layoutManager);

        rootLayout = findViewById(R.id.rootLayout);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDealDialog();

            }
        });

        //get intent from prev page
        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId != null) {
            loadListItem(categoryId);
        }

    }

    private void showAddDealDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItemList.this);
        alertDialog.setTitle("Add new deal!");
        alertDialog.setMessage("Please fill in all the information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_deal_layout, null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtLocation = add_menu_layout.findViewById(R.id.edtLocation);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();

            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_face_orange_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                if(newItem != null){
                    itemList.push().setValue(newItem);
                    Snackbar.make(rootLayout, "New Deal: "+newItem.getName()+" has been successfully added!",Snackbar.LENGTH_LONG)
                            .show();
                }


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data !=null && data.getData() != null){
            saveUri = data.getData();
            btnSelect.setText("Image Selected!");
        }
    }

    private void uploadImage() {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(ItemList.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newItem = new Item();
                            newItem.setName(edtName.getText().toString());
                            newItem.setDescription(edtDescription.getText().toString());
                            newItem.setLocation(edtLocation.getText().toString());
                            newItem.setMenuId(categoryId);
                            newItem.setImage(uri.toString());

                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(ItemList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mDialog.setMessage(progress+"% Uploaded");

                }
            });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    private void loadListItem(String categoryId) {
            adapter = new FirebaseRecyclerAdapter<Item, DealViewHolder>
                    (Item.class, R.layout.deal_item, DealViewHolder.class, itemList.orderByChild("menuId").equalTo(categoryId)) {
                @Override
                protected void populateViewHolder(DealViewHolder viewHolder, Item model, int position) {
                    viewHolder.deal_name.setText(model.getName());
                    Picasso.get().load(model.getImage()).into(viewHolder.deal_image);

                    final Item local = model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Toast.makeText(ItemList.this, ""+local.getName(),Toast.LENGTH_SHORT).show();

                            Intent itemDetail = new Intent(ItemList.this, DealItem.class);
                        itemDetail.putExtra("ItemId", adapter.getRef(position).getKey()); //send item id to new activity
                        startActivity(itemDetail);
                        }
                    });

                }
            };
            adapter.notifyDataSetChanged();
        recycler_item.setAdapter(adapter);

    }
}
