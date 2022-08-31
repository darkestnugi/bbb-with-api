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
import com.example.bbbwithapi.model.Absence;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AbsenceListAdapter extends RecyclerView.Adapter<AbsenceListAdapter.ViewHolder> {
    private ArrayList<Absence> listAbsence;
    private Context context;

    public AbsenceListAdapter(ArrayList<Absence> listAbsence, Context context) {
        this.listAbsence = listAbsence;
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
        holder.tvItemAbsenceTitle.setText(listAbsence.get(position).getTanggalAbsen() + " " + listAbsence.get(position).getMonth() + " " + listAbsence.get(position).getTahunAbsen());
        holder.tvNameList.setText(listAbsence.get(position).getName() + " " + listAbsence.get(position).getMobilePhone());
        holder.tvDivisonList.setText(listAbsence.get(position).getDivision());

        if (position == listAbsence.size()-1) {
            holder.viewSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listAbsence.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemAbsenceTitle, tvNameList, tvDivisonList;
        private View viewSeparator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemAbsenceTitle = (TextView) itemView.findViewById(R.id.tvItemAbsenceTitle);
            tvNameList = (TextView) itemView.findViewById(R.id.tvItemAbsenceName);
            tvDivisonList = (TextView) itemView.findViewById(R.id.tvItemAbsenceDivision);
            viewSeparator = (View) itemView.findViewById(R.id.viewSeparatorAbsence);
        }
    }
}
