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
import com.example.bbbwithapi.model.ReportPDCPersonal;

import java.util.ArrayList;

public class ReportPDCPersonalListAdapter extends RecyclerView.Adapter<ReportPDCPersonalListAdapter.ViewHolder> {
    private ArrayList<ReportPDCPersonal> listReportPersonal;
    private Context context;

    public ReportPDCPersonalListAdapter(ArrayList<ReportPDCPersonal> listReportPersonal, Context context) {
        this.listReportPersonal = listReportPersonal;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_list_total_pdc, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvYearAndMonth.setText(listReportPersonal.get(position).getMonth() + " " + listReportPersonal.get(position).getTahunTransaksi());
        holder.tvItemSuccessTotalAmount.setText(String.format("%,d", Integer.parseInt(listReportPersonal.get(position).getJumlahSuccess().toString())) + " orang");
        holder.tvItemSuccess1MillionTotalAmount.setText(String.format("%,d", Integer.parseInt(listReportPersonal.get(position).getJumlahSuccessDonorMoreThan1Million().toString())) + " orang");
        holder.tvItemNominalTotalAmount.setText(String.format("Rp %,.0f", Double.parseDouble(listReportPersonal.get(position).getJumlahTransaksi().toString())));

        if (position == listReportPersonal.size()-1) {
            holder.viewSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listReportPersonal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvYearAndMonth, tvItemSuccessTotalAmount, tvItemSuccess1MillionTotalAmount, tvItemNominalTotalAmount;
        private View viewSeparator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvYearAndMonth = (TextView) itemView.findViewById(R.id.tvItemPDCTitle);
            tvItemSuccessTotalAmount = (TextView) itemView.findViewById(R.id.tvItemSuccessTotalAmount);
            tvItemSuccess1MillionTotalAmount = (TextView) itemView.findViewById(R.id.tvItemSuccess1MillionTotalAmount);
            tvItemNominalTotalAmount = (TextView) itemView.findViewById(R.id.tvItemNominalTotalAmount);
            viewSeparator = (View) itemView.findViewById(R.id.viewSeparatorReport);
        }
    }
}
