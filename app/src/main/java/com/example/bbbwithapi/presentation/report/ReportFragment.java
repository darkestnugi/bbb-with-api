package com.example.bbbwithapi.presentation.report;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.model.Report;
import com.example.bbbwithapi.model.ReportPDC;
import com.example.bbbwithapi.model.ReportPDCPersonal;
import com.example.bbbwithapi.model.ReportPersonal;
import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.login.LoginActivity;
import com.example.bbbwithapi.presentation.report.adapter.ReportListAdapter;
import com.example.bbbwithapi.presentation.report.adapter.ReportPDCListAdapter;
import com.example.bbbwithapi.presentation.report.adapter.ReportPDCPersonalListAdapter;
import com.example.bbbwithapi.presentation.report.adapter.ReportPersonalListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

@RequiresApi(api = Build.VERSION_CODES.N)
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    private RecyclerView rvList;
    private Toolbar toolbarReport;
    private TextView tvUploadReport;
    private View viewContainer;

    private ArrayList<Report> listReport = new ArrayList<>();
    private ArrayList<ReportPersonal> listReportPersonal = new ArrayList<>();
    private ArrayList<ReportPDC> listReportPDC = new ArrayList<>();
    private ArrayList<ReportPDCPersonal> listReportPDCPersonal = new ArrayList<>();

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        defineIds(inflater, container);
        initUi();
        initAction();
        return viewContainer;
    }

    private void defineIds(LayoutInflater inflater, ViewGroup container) {
        viewContainer = inflater.inflate(R.layout.fragment_report, container, false);
        toolbarReport = (Toolbar) viewContainer.findViewById(R.id.toolbarReport);
        rvList = (RecyclerView) viewContainer.findViewById(R.id.rvTotalDonation);
        tvUploadReport = (TextView) viewContainer.findViewById(R.id.tvToolbarUploadReport);

        mycontext = requireActivity();
        prefManager = new PrefManager(requireContext());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            prefManager.removeAllPreference();
            mAuth.signOut();

            Toast.makeText(mycontext, "Waktu login Anda habis. Silakan Login kembali (1)", Toast.LENGTH_LONG).show();
            startActivity(new Intent(mycontext, LoginActivity.class));
            requireActivity().finish();
        } else {
            mUser = mAuth.getCurrentUser();
            mUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                // Send token to your backend via HTTPS
                            } else {
                                prefManager.removeAllPreference();
                                mAuth.signOut();

                                Toast.makeText(mycontext, "Waktu login Anda habis. Silakan Login kembali (2)", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(mycontext, LoginActivity.class));
                                requireActivity().finish();
                            }
                        }
                    });
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        mFirebaseAnalytics.setUserProperty("userID", prefManager.getUserID());
        mFirebaseAnalytics.setUserProperty("userEmail", prefManager.getEmail());

        crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("userID", prefManager.getUserID());
        crashlytics.setCustomKey("userEmail", prefManager.getEmail());
    }

    private void initUi() {
        if (getString(R.string.is_use_upload_donation).equals("Yes")) {
            //getLaporanTahunan();
            getLaporanTahunanPribadi();
        }

        if (getString(R.string.is_use_upload_pdc).equals("Yes")) {
            //getLaporanPDCTahunan();
            getLaporanPDCTahunanPribadi();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initAction() {
        tvUploadReport.setOnClickListener(view -> {
            Intent intent = new Intent(this.requireContext(), ReportDetailActivity.class);
            startActivity(intent);
        });
    }

    private void getLaporanTahunan() {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Downloading");
        progressDialog.show();
        progressDialog.setMessage("Downloaded " + 50 + "%...");

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("donationyearly").orderByChild("jumlahTransaksi").limitToLast(1000);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                listReport.clear();
                long count = 0;
                long total = dataSnapshot.getChildrenCount();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Report artist = postSnapshot.getValue(Report.class);

                    listReport.add(artist);

                    count++;
                    double progress = (100.0 * count) / total;
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                }

                Collections.sort(listReport);

                progressDialog.dismiss();
                rvList.setAdapter(new ReportListAdapter(listReport, mycontext));
                rvList.setLayoutManager(new LinearLayoutManager(mycontext));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                double progress = 100.0;
                progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                progressDialog.dismiss();
            }
        });
    }

    private void getLaporanTahunanPribadi() {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Downloading");
        progressDialog.show();
        progressDialog.setMessage("Downloaded " + 50 + "%...");

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("donationyearlypersonal").orderByChild("userID").equalTo(prefManager.getUserID()).limitToLast(1000);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                listReportPersonal.clear();
                long count = 0;
                long total = dataSnapshot.getChildrenCount();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    ReportPersonal artist = postSnapshot.getValue(ReportPersonal.class);

                    listReportPersonal.add(artist);

                    count++;
                    double progress = (100.0 * count) / total;
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                }

                Collections.sort(listReportPersonal);

                progressDialog.dismiss();
                rvList.setAdapter(new ReportPersonalListAdapter(listReportPersonal, mycontext));
                rvList.setLayoutManager(new LinearLayoutManager(mycontext));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                double progress = 100.0;
                progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                progressDialog.dismiss();
            }
        });
    }

    private void getLaporanPDCTahunan() {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Downloading");
        progressDialog.show();
        progressDialog.setMessage("Downloaded " + 50 + "%...");

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("pdcyearly").orderByChild("jumlahTransaksi").limitToLast(1000);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                listReportPDC.clear();
                long count = 0;
                long total = dataSnapshot.getChildrenCount();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    ReportPDC artist = postSnapshot.getValue(ReportPDC.class);

                    listReportPDC.add(artist);

                    count++;
                    double progress = (100.0 * count) / total;
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                }

                Collections.sort(listReportPDC);

                progressDialog.dismiss();
                rvList.setAdapter(new ReportPDCListAdapter(listReportPDC, mycontext));
                rvList.setLayoutManager(new LinearLayoutManager(mycontext));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                double progress = 100.0;
                progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                progressDialog.dismiss();
            }
        });
    }

    private void getLaporanPDCTahunanPribadi() {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Downloading");
        progressDialog.show();
        progressDialog.setMessage("Downloaded " + 50 + "%...");

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("pdcyearlypersonal").orderByChild("userID").equalTo(prefManager.getUserID()).limitToLast(1000);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                listReportPDCPersonal.clear();
                long count = 0;
                long total = dataSnapshot.getChildrenCount();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    ReportPDCPersonal artist = postSnapshot.getValue(ReportPDCPersonal.class);

                    listReportPDCPersonal.add(artist);

                    count++;
                    double progress = (100.0 * count) / total;
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                }

                Collections.sort(listReportPDCPersonal);

                progressDialog.dismiss();
                rvList.setAdapter(new ReportPDCPersonalListAdapter(listReportPDCPersonal, mycontext));
                rvList.setLayoutManager(new LinearLayoutManager(mycontext));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                double progress = 100.0;
                progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                progressDialog.dismiss();
            }
        });
    }
}
