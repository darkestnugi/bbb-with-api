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
import com.example.bbbwithapi.model.WatchYearly;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class WatchYearlyListAdapter extends RecyclerView.Adapter<WatchYearlyListAdapter.ViewHolder> {
    private ArrayList<WatchYearly> listWatchYearly;
    private Context context;

    public WatchYearlyListAdapter(ArrayList<WatchYearly> listWatchYearly, Context context) {
        this.listWatchYearly = listWatchYearly;
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
        holder.tvItemWatchYearlyTitle.setText(listWatchYearly.get(position).getTanggalPiket() + " " + listWatchYearly.get(position).getMonth() + " " + listWatchYearly.get(position).getTahunPiket());

        Integer index = 0;
        String namaTelepon = "";
        String divisi = "";

        for(String x : listWatchYearly.get(position).getName()) {
            String telepon = listWatchYearly.get(position).getMobilePhone().get(index);

            if (namaTelepon.equals("") || namaTelepon == null) {
                namaTelepon = x + " " + telepon;
            } else {
                namaTelepon = namaTelepon + "\n" + x + " " + telepon;
            }

            index++;
        }

        index = 0;
        for(String y : listWatchYearly.get(position).getDivision()) {
            if (divisi.equals("") || divisi == null) {
                divisi = y;
            } else {
                divisi = divisi + "\n" + y;
            }

            index++;
        }

        holder.tvNameList.setText(namaTelepon);
        holder.tvDivisonList.setText(divisi);

        if (position == listWatchYearly.size()-1) {
            holder.viewSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listWatchYearly.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemWatchYearlyTitle, tvNameList, tvDivisonList;
        private View viewSeparator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemWatchYearlyTitle = (TextView) itemView.findViewById(R.id.tvItemWatchTitle);
            tvNameList = (TextView) itemView.findViewById(R.id.tvItemWatchName);
            tvDivisonList = (TextView) itemView.findViewById(R.id.tvItemWatchDivision);
            viewSeparator = (View) itemView.findViewById(R.id.viewSeparatorWatch);
        }
    }
}
