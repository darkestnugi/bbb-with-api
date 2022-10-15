package com.example.bbbwithapi.presentation.donation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bbbwithapi.R;
import com.example.bbbwithapi.model.Donation;
import com.example.bbbwithapi.presentation.donation.DonationDetailActivity;
import com.example.bbbwithapi.presentation.donation.DonationImageActivity;
import com.example.bbbwithapi.presentation.donation.DonationInvoiceActivity;
import com.example.bbbwithapi.utils.IntentKeyUtils;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DonationListAdapter extends RecyclerView.Adapter<DonationListAdapter.ViewHolder> {

    private ArrayList<Donation> listItems;
    private Context context;

    public DonationListAdapter(ArrayList<Donation> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_donation_list, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(listItems.get(position).getPhotoURL()).into(holder.ivItemImgDonation);

        holder.tvItemAmount.setText(String.format("Rp %,.0f", listItems.get(position).getNominal()));
        holder.tvTransactionStatus.setText(listItems.get(position).getStatusPayment());
        holder.tvTransactionDate.setText(listItems.get(position).getTransactionDate().substring(0, 10));

        if (listItems.get(position).getStatusPayment().equals("Menunggu Verifikasi")) {
            holder.btnEditDonation.setText("Ubah Donasi");
            holder.btnEditDonation.setVisibility(View.VISIBLE);
        } else if (listItems.get(position).getStatusPayment().equals("Gagal Diverifikasi")) {
            holder.btnEditDonation.setText("Ubah Donasi Yang Gagal");
            holder.btnEditDonation.setVisibility(View.VISIBLE);
        } else if (listItems.get(position).getStatusPayment().equals("Berhasil Diverifikasi")) {
            holder.btnEditDonation.setText("Lihat Invoice Donasi");
            holder.btnEditDonation.setVisibility(View.VISIBLE);
        }

        if (listItems.get(position).getNotePayment() == null
                || listItems.get(position).getNotePayment().equals("")
                || listItems.get(position).getNotePayment().length() == 0
        ) {
            holder.tvTransactionNote.setText("-");
        } else {
            holder.tvTransactionNote.setText(listItems.get(position).getNotePayment());
        }

        holder.actionItem(position);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImgDonation;
        TextView tvItemAmount, tvTransactionNote, tvTransactionDate, tvTransactionStatus;
        Button btnEditDonation;
        CardView cvDonationItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImgDonation = (ImageView) itemView.findViewById(R.id.ivImageListDonation);
            tvItemAmount = (TextView) itemView.findViewById(R.id.tvTotalAmount);
            tvTransactionNote = (TextView) itemView.findViewById(R.id.tvTransactionNote);
            tvTransactionDate = (TextView) itemView.findViewById(R.id.tvTransactionDate);
            tvTransactionStatus = (TextView) itemView.findViewById(R.id.tvTransactionStatus);
            btnEditDonation = (Button) itemView.findViewById(R.id.btnEditDonation);
            cvDonationItems = (CardView) itemView.findViewById(R.id.cvDonationItems);
        }

        public void actionItem(int position){
            Donation mydata = listItems.get(position);

            if (mydata.getStatusPayment().equals("Menunggu Verifikasi")) {
                btnEditDonation.setOnClickListener(view -> {
                    Intent intent = new Intent(context, DonationDetailActivity.class);
                    intent.putExtra("donation_id", String.valueOf(listItems.get(position).getID()));
                    intent.putExtra(IntentKeyUtils.keyDetailDonationDetailData, mydata);
                    context.startActivity(intent);
                });
            } else if (mydata.getStatusPayment().equals("Gagal Diverifikasi")) {
                btnEditDonation.setOnClickListener(view -> {
                    Intent intent = new Intent(context, DonationDetailActivity.class);
                    intent.putExtra("donation_id", String.valueOf(listItems.get(position).getID()));
                    intent.putExtra(IntentKeyUtils.keyDetailDonationDetailData, mydata);
                    context.startActivity(intent);
                });
            } else if (mydata.getStatusPayment().equals("Berhasil Diverifikasi")) {
                btnEditDonation.setOnClickListener(view -> {
                    Intent intent = new Intent(context, DonationInvoiceActivity.class);
                    intent.putExtra("donation_id", String.valueOf(listItems.get(position).getID()));
                    intent.putExtra(IntentKeyUtils.keyDetailDonationDetailReceipt, mydata);
                    context.startActivity(intent);
                });
            }

            cvDonationItems.setOnClickListener(view -> {
                Intent intent = new Intent(context, DonationImageActivity.class);
                intent.putExtra("donation_id", String.valueOf(listItems.get(position).getID()));
                intent.putExtra(IntentKeyUtils.keyDetailDonationDetailImage, mydata);
                context.startActivity(intent);
            });
        }
    }
}
