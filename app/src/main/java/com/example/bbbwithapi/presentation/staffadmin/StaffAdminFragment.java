package com.example.bbbwithapi.presentation.staffadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.BBBActivity;
import com.example.bbbwithapi.model.Management;
import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.login.LoginActivity;
import com.example.bbbwithapi.presentation.staffadmin.adapter.StaffAdminAdapter;
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

@RequiresApi(api = Build.VERSION_CODES.N)
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaffAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaffAdminFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Management> listManagement = new ArrayList<Management>();

    private RecyclerView rvStaffAdmin;
    private View viewContainer;
    private TextView tvCloseImageDetail;
    private Group groupDetail;
    private ImageView ivDetail;

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    public StaffAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StaffAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StaffAdminFragment newInstance(String param1, String param2) {
        StaffAdminFragment fragment = new StaffAdminFragment();
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

        viewContainer = inflater.inflate(R.layout.fragment_staff_admin, container, false);

        defineId();
        addListItems();
        initAction();

        return viewContainer;
    }

    private void initAction() {
        tvCloseImageDetail.setOnClickListener(view -> {
            groupDetail.setVisibility(View.GONE);
            rvStaffAdmin.setVisibility((View.VISIBLE));
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (groupDetail.getVisibility() == View.VISIBLE) {
                    groupDetail.setVisibility(View.GONE);
                    rvStaffAdmin.setVisibility(View.VISIBLE);
                } else {
                    if (!StaffAdminFragment.this.isHidden()){
                        requireActivity().finish();
                    }
                }
            }
        });
    }

    private void defineId() {
        rvStaffAdmin = (RecyclerView) viewContainer.findViewById(R.id.rvStaffAdmin);
        tvCloseImageDetail = (TextView) viewContainer.findViewById(R.id.tvCloseImageStaffAdmin);
        groupDetail = (Group) viewContainer.findViewById(R.id.groupDetails);
        ivDetail = (ImageView) viewContainer.findViewById(R.id.ivDetailImageStaffAdmin);

        mycontext = requireActivity();
        prefManager = new PrefManager(requireContext());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            prefManager.removeAllPreference();
            mAuth.signOut();

            Toast.makeText(mycontext, "Waktu login Anda habis. Silakan Login kembali", Toast.LENGTH_LONG).show();
            startActivity(new Intent(mycontext, LoginActivity.class));
        }

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

                            Toast.makeText(mycontext, "Waktu login Anda habis. Silakan Login kembali", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(mycontext, LoginActivity.class));
                        }
                    }
                });

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

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("management").limitToLast(360000);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listManagement.clear();

                long count = 0;
                long totalChildren = snapshot.getChildrenCount();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Management artist = postSnapshot.getValue(Management.class);

                    if (artist.getIsActive()) {
                        listManagement.add(artist);
                    }

                    count++;
                    double progress = (100.0 * count) / totalChildren;
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                }

                progressDialog.dismiss();
                rvStaffAdmin.setAdapter(new StaffAdminAdapter(listManagement, groupDetail, ivDetail, (BBBActivity)getActivity(), rvStaffAdmin));
                rvStaffAdmin.setLayoutManager(new LinearLayoutManager(mycontext));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                double progress = 100.0;
                progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                progressDialog.dismiss();
            }
        });
    }
}
