package com.example.bbbwithapi.presentation.report.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.model.Report;

import java.util.ArrayList;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ViewHolder> {
    private ArrayList<Report> listReport;
    private Context context;

    public ReportListAdapter(ArrayList<Report> listReport, Context context) {
        this.listReport = listReport;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_list_total_donation, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvYearAndMonth.setText(listReport.get(position).getMonth() + " " + listReport.get(position).getTahunTransaksi());
        holder.tvTotalDonationAmount.setText(String.format("Rp %,.0f", Double.parseDouble(listReport.get(position).getJumlahTransaksi().toString())));

        if (position == listReport.size()-1) {
            holder.viewSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listReport.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvYearAndMonth, tvTotalDonationAmount;
        private View viewSeparator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvYearAndMonth = (TextView) itemView.findViewById(R.id.tvItemDonationTitle);
            tvTotalDonationAmount = (TextView) itemView.findViewById(R.id.tvItemDonationTotalAmount);
            viewSeparator = (View) itemView.findViewById(R.id.viewSeparatorReport);
        }
    }
}
