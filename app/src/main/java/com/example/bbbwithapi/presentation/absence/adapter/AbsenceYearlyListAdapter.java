package com.example.bbbwithapi.presentation.absence.adapter;

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
import com.example.bbbwithapi.model.AbsenceYearly;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AbsenceYearlyListAdapter extends RecyclerView.Adapter<AbsenceYearlyListAdapter.ViewHolder> {
    private ArrayList<AbsenceYearly> listAbsenceYearly;
    private Context context;

    public AbsenceYearlyListAdapter(ArrayList<AbsenceYearly> listAbsenceYearly, Context context) {
        this.listAbsenceYearly = listAbsenceYearly;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_list_total_absence, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvItemAbsenceYearlyTitle.setText(listAbsenceYearly.get(position).getTanggalAbsen() + " " + listAbsenceYearly.get(position).getMonth() + " " + listAbsenceYearly.get(position).getTahunAbsen());

        Integer index = 0;
        String namaTelepon = "";
        String divisi = "";

        for(String x : listAbsenceYearly.get(position).getName()) {
            String telepon = listAbsenceYearly.get(position).getMobilePhone().get(index);

            if (namaTelepon.equals("") || namaTelepon == null) {
                namaTelepon = x + " " + telepon;
            } else {
                namaTelepon = namaTelepon + "\n" + x + " " + telepon;
            }

            index++;
        }

        index = 0;
        for(String y : listAbsenceYearly.get(position).getDivision()) {
            if (divisi.equals("") || divisi == null) {
                divisi = y;
            } else {
                divisi = divisi + "\n" + y;
            }

            index++;
        }

        holder.tvNameList.setText(namaTelepon);
        holder.tvDivisonList.setText(divisi);

        if (position == listAbsenceYearly.size()-1) {
            holder.viewSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listAbsenceYearly.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemAbsenceYearlyTitle, tvNameList, tvDivisonList;
        private View viewSeparator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemAbsenceYearlyTitle = (TextView) itemView.findViewById(R.id.tvItemAbsenceTitle);
            tvNameList = (TextView) itemView.findViewById(R.id.tvItemAbsenceName);
            tvDivisonList = (TextView) itemView.findViewById(R.id.tvItemAbsenceDivision);
            viewSeparator = (View) itemView.findViewById(R.id.viewSeparatorAbsence);
        }
    }
}
