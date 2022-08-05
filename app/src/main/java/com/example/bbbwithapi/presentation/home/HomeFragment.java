package com.example.bbbwithapi.presentation.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.model.Program;
import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.home.adapter.HomeListAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rvHome;
    private View viewContainer;

    private Context context;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    private ArrayList<Program> listProgram = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        defineIds(inflater, container);
        addListItems();
        initUi();
        return viewContainer;
    }

    private void defineIds(LayoutInflater inflater, ViewGroup viewGroup) {
        viewContainer = inflater.inflate(R.layout.fragment_home, viewGroup, false);
        rvHome = (RecyclerView) viewContainer.findViewById(R.id.rvHome);

        context = requireActivity();
        prefManager = new PrefManager(requireContext());

        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        mFirebaseAnalytics.setUserProperty("userID", prefManager.getUserID());
        mFirebaseAnalytics.setUserProperty("userEmail", prefManager.getEmail());

        crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("userID", prefManager.getUserID());
        crashlytics.setCustomKey("userEmail", prefManager.getEmail());
    }

    private void addListItems() {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Downloading");
        progressDialog.show();
        progressDialog.setMessage("Downloaded " + 50 + "%...");

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("program").limitToLast(360000);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProgram.clear();

                long count = 0;
                long totalChildren = snapshot.getChildrenCount();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Program artist = postSnapshot.getValue(Program.class);

                    if (artist.getIsActive()) {
                        listProgram.add(artist);
                    }

                    count++;
                    double progress = (100.0 * count) / totalChildren;
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                }

                progressDialog.dismiss();
                rvHome.setAdapter(new HomeListAdapter(listProgram, context));
                rvHome.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                double progress = 100.0;
                progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                progressDialog.dismiss();
            }
        });
    }

    private void initUi() {

    }
}
