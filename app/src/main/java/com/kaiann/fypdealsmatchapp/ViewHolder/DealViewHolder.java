package com.kaiann.fypdealsmatchapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaiann.fypdealsmatchapp.Interface.ItemClickListener;
import com.kaiann.fypdealsmatchapp.R;

public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView deal_name;
    public ImageView deal_image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;

    }

    public DealViewHolder(@NonNull View itemView) {
        super(itemView);

        deal_name = itemView.findViewById(R.id.deal_name);
        deal_image = itemView.findViewById(R.id.deal_image);

        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
