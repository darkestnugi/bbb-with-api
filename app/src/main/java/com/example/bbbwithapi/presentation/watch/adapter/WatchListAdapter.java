package com.example.bbbwithapi.presentation.watch.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.model.Watch;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.ViewHolder> {
    private ArrayList<Watch> listWatch;
    private Context context;

    public WatchListAdapter(ArrayList<Watch> listWatch, Context context) {
        this.listWatch = listWatch;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_list_total_watch, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvItemWatchTitle.setText(listWatch.get(position).getTanggalPiket() + " " + listWatch.get(position).getMonth() + " " + listWatch.get(position).getTahunPiket());
        holder.tvNameList.setText(listWatch.get(position).getName() + " " + listWatch.get(position).getMobilePhone());
        holder.tvDivisonList.setText(listWatch.get(position).getDivision());

        if (position == listWatch.size()-1) {
            holder.viewSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listWatch.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemWatchTitle, tvNameList, tvDivisonList;
        private View viewSeparator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemWatchTitle = (TextView) itemView.findViewById(R.id.tvItemWatchTitle);
            tvNameList = (TextView) itemView.findViewById(R.id.tvItemWatchName);
            tvDivisonList = (TextView) itemView.findViewById(R.id.tvItemWatchDivision);
            viewSeparator = (View) itemView.findViewById(R.id.viewSeparatorWatch);
        }
    }
}
