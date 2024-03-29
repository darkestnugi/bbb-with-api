package com.example.bbbwithapi.presentation.report.adapter;

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
import com.example.bbbwithapi.model.ReportPersonal;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ReportPersonalListAdapter extends RecyclerView.Adapter<ReportPersonalListAdapter.ViewHolder> {
    private ArrayList<ReportPersonal> listReportPersonal;
    private Context context;

    public ReportPersonalListAdapter(ArrayList<ReportPersonal> listReportPersonal, Context context) {
        this.listReportPersonal = listReportPersonal;
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
        holder.tvYearAndMonth.setText(listReportPersonal.get(position).getMonth() + " " + listReportPersonal.get(position).getTahunTransaksi());
        holder.tvTotalDonationAmount.setText(String.format("Rp %,.0f", Double.parseDouble(listReportPersonal.get(position).getJumlahTransaksi().toString())));

        if (position == listReportPersonal.size()-1) {
            holder.viewSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listReportPersonal.size();
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
