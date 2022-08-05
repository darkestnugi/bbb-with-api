package com.example.bbbwithapi.presentation.staffadmin.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bbbwithapi.R;
import com.example.bbbwithapi.BBBActivity;
import com.example.bbbwithapi.model.Management;

import java.util.ArrayList;

public class StaffAdminAdapter extends RecyclerView.Adapter<StaffAdminAdapter.ViewHolder> {

    private ArrayList<Management> listItems;

    private Context context;
    private ImageView ivDetailImage;
    private Group groupDetail;
    private AppCompatActivity parentActivity;
    private RecyclerView rvListItems;

    public StaffAdminAdapter(ArrayList<Management> listItems, Group groupDetail, ImageView iv, Activity activity, RecyclerView rv) {
        this.listItems = listItems;
        this.ivDetailImage = iv;
        this.groupDetail = groupDetail;
        this.parentActivity = (BBBActivity) activity;
        this.rvListItems = rv;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_staff_admin, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = listItems.get(position).getTitle();
        String photoURL = listItems.get(position).getPhotoURL();

        ImageView ivHolder = holder.ivImgStaffAdmin;
        TextView tvHolder = holder.tvItemStaffAdmin;

        Glide.with(context).load(photoURL).centerCrop().into(ivHolder);
        tvHolder.setText(title);

        holder.actionItem(position);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImgStaffAdmin;
        TextView tvItemStaffAdmin;
        CardView cvItemsStaffAdmin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImgStaffAdmin = (ImageView) itemView.findViewById(R.id.ivItemStaffAdmin);
            tvItemStaffAdmin = (TextView) itemView.findViewById(R.id.tvItemStaffAdmin);
            cvItemsStaffAdmin = (CardView) itemView.findViewById(R.id.cvItemStaffAdmin);
        }

        public void actionItem(int position){
            cvItemsStaffAdmin.setOnClickListener(view -> {
                Glide.with(context).load(listItems.get(position).getPhotoURL()).centerCrop().into(ivDetailImage);
                groupDetail.setVisibility(View.VISIBLE);
                rvListItems.setVisibility(View.GONE);
            });
        }
    }
}
