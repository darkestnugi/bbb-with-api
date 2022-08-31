package com.example.bbbwithapi.presentation.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bbbwithapi.R;
import com.example.bbbwithapi.model.Program;
import com.example.bbbwithapi.presentation.homedetail.DetailHomeProgramsActivity;
import com.example.bbbwithapi.utils.IntentKeyUtils;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {
    private ArrayList<Program> listItems;
    private Context context;

    public HomeListAdapter(ArrayList<Program> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_home, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView ivHolder = holder.ivItemImgHome;
        TextView tvTitleItemHolder = holder.tvItemHomeTitle;
        TextView tvDescItemHolder = holder.tvItemHomeDesc;

        Glide.with(context).load(listItems.get(position).getPhotoURL()).centerCrop().into(ivHolder);
        tvTitleItemHolder.setText(listItems.get(position).getTitle());
        tvDescItemHolder.setText(listItems.get(position).getDescription());

        holder.actionItem(position);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImgHome;
        TextView tvItemHomeTitle, tvItemHomeDesc;
        CardView cvItemHome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImgHome = (ImageView) itemView.findViewById(R.id.ivItemHomeImage);
            tvItemHomeTitle = (TextView) itemView.findViewById(R.id.tvItemTitleHomePrograms);
            tvItemHomeDesc = (TextView) itemView.findViewById(R.id.tvItemDescHomePrograms);
            cvItemHome = (CardView) itemView.findViewById(R.id.cvItemHome);
        }

        public void actionItem(int position){
            cvItemHome.setOnClickListener(view -> {
                Intent intent = new Intent(context, DetailHomeProgramsActivity.class);
                intent.putExtra(IntentKeyUtils.keyDetailProgramImage, listItems.get(position).getPhotoURL());
                intent.putExtra(IntentKeyUtils.keyDetailProgramTitle, listItems.get(position).getTitle());
                intent.putExtra(IntentKeyUtils.keyDetailProgramDesc, listItems.get(position).getDescription());

                context.startActivity(intent);
            });
        }
    }
}
