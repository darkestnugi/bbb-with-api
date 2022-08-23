package com.example.bbbwithapi.presentation.watch;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.model.Watch;
import com.example.bbbwithapi.model.WatchYearly;
import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.watch.adapter.WatchListAdapter;
import com.example.bbbwithapi.presentation.watch.adapter.WatchYearlyListAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class WatchActivity extends AppCompatActivity{
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    public static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    public ImageView ivBackIcon;
    private RecyclerView rvList;
    private Toolbar toolbarWatch;
    private TextView tvUploadWatch;

    private ArrayList<Watch> listWatch = new ArrayList<>();
    private ArrayList<WatchYearly> listWatchYearly = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        askPermission();
        defineIds();
        initUi();
        initAction();
    }

    private void defineIds() {
        toolbarWatch = (Toolbar) findViewById(R.id.toolbarReport);
        rvList = (RecyclerView) findViewById(R.id.rvTotalWatch);
        tvUploadWatch = (TextView) findViewById(R.id.tvToolbarUploadWatch);
        ivBackIcon = (ImageView) findViewById(R.id.ivBackButtonToolbar);

        mycontext = WatchActivity.this;
        prefManager = new PrefManager(mycontext);
        mAuth = FirebaseAuth.getInstance();
        
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mycontext);
        mFirebaseAnalytics.setUserProperty("userID", prefManager.getUserID());
        mFirebaseAnalytics.setUserProperty("userEmail", prefManager.getEmail());

        crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("userID", prefManager.getUserID());
        crashlytics.setCustomKey("userEmail", prefManager.getEmail());
    }

    private void initUi() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (getString(R.string.is_use_upload_watch).equals("Yes")) {
            //getDaftarPiket();
            getDaftarPiketTahunan();
        }
    }

    private void initAction() {
        ivBackIcon.setOnClickListener(view -> {
            finish();
        });

        tvUploadWatch.setOnClickListener(view -> {
            Intent intent = new Intent(WatchActivity.this, WatchDetailActivity.class);
            startActivity(intent);
        });
    }

    private void getDaftarPiket() {
        final ProgressDialog progressDialog = new ProgressDialog(mycontext);
        progressDialog.setTitle("Downloading");
        progressDialog.show();
        progressDialog.setMessage("Downloaded " + 50 + "%...");

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("watch").limitToLast(360000);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                listWatch.clear();

                long count = 0;
                long total = dataSnapshot.getChildrenCount();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Watch artist = postSnapshot.getValue(Watch.class);

                    listWatch.add(artist);

                    count++;
                    double progress = (100.0 * count) / total;
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                }

                Collections.sort(listWatch);

                progressDialog.dismiss();
                rvList.setAdapter(new WatchListAdapter(listWatch, mycontext));
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

    private void getDaftarPiketTahunan() {
        final ProgressDialog progressDialog = new ProgressDialog(mycontext);
        progressDialog.setTitle("Downloading");
        progressDialog.show();
        progressDialog.setMessage("Downloaded " + 50 + "%...");

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("watchyearly").limitToLast(360000);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                listWatchYearly.clear();

                long count = 0;
                long total = dataSnapshot.getChildrenCount();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    WatchYearly artist = postSnapshot.getValue(WatchYearly.class);

                    listWatchYearly.add(artist);

                    count++;
                    double progress = (100.0 * count) / total;
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                }

                Collections.sort(listWatchYearly);

                progressDialog.dismiss();
                rvList.setAdapter(new WatchYearlyListAdapter(listWatchYearly, mycontext));
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

    private void askPermission() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    WRITE_EXTERNAL_STORAGE_CODE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    READ_EXTERNAL_STORAGE_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.INTERNET)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    INTERNET,
                    Manifest.permission.INTERNET);
        }

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    ACCESS_FINE_LOCATION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    ACCESS_COARSE_LOCATION_CODE,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }
}
