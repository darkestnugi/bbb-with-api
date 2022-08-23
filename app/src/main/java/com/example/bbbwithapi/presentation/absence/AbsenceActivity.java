package com.example.bbbwithapi.presentation.absence;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.model.Absence;
import com.example.bbbwithapi.model.AbsenceYearly;
import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.absence.adapter.AbsenceListAdapter;
import com.example.bbbwithapi.presentation.absence.adapter.AbsenceYearlyListAdapter;
import com.example.bbbwithapi.presentation.login.LoginActivity;
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

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AbsenceActivity extends AppCompatActivity{
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
    private Toolbar toolbarAbsence;
    private TextView tvUploadAbsence;

    private ArrayList<Absence> listAbsence = new ArrayList<>();
    private ArrayList<AbsenceYearly> listAbsenceYearly = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence);

        askPermission();
        defineIds();
        initUi();
        initAction();
    }

    private void defineIds() {
        toolbarAbsence = (Toolbar) findViewById(R.id.toolbarReport);
        rvList = (RecyclerView) findViewById(R.id.rvTotalAbsence);
        tvUploadAbsence = (TextView) findViewById(R.id.tvToolbarUploadAbsence);
        ivBackIcon = (ImageView) findViewById(R.id.ivBackButtonToolbar);

        mycontext = this;
        prefManager = new PrefManager(mycontext);
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

        if (getString(R.string.is_use_upload_absence).equals("Yes")) {
            //getDaftarAbsen();
            getDaftarAbsenTahunan();
        }
    }

    private void initAction() {
        ivBackIcon.setOnClickListener(view -> {
            finish();
        });

        tvUploadAbsence.setOnClickListener(view -> {
            Intent intent = new Intent(AbsenceActivity.this, AbsenceDetailActivity.class);
            startActivity(intent);
        });
    }

    private void getDaftarAbsen() {
        final ProgressDialog progressDialog = new ProgressDialog(mycontext);
        progressDialog.setTitle("Downloading");
        progressDialog.show();
        progressDialog.setMessage("Downloaded " + 50 + "%...");

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("absence").limitToLast(360000);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                listAbsence.clear();

                long count = 0;
                long total = dataSnapshot.getChildrenCount();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Absence artist = postSnapshot.getValue(Absence.class);

                    listAbsence.add(artist);

                    count++;
                    double progress = (100.0 * count) / total;
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                }

                Collections.sort(listAbsence);

                progressDialog.dismiss();
                rvList.setAdapter(new AbsenceListAdapter(listAbsence, mycontext));
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

    private void getDaftarAbsenTahunan() {
        final ProgressDialog progressDialog = new ProgressDialog(mycontext);
        progressDialog.setTitle("Downloading");
        progressDialog.show();
        progressDialog.setMessage("Downloaded " + 50 + "%...");

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("absenceyearly").limitToLast(360000);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                listAbsenceYearly.clear();

                long count = 0;
                long total = dataSnapshot.getChildrenCount();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    AbsenceYearly artist = postSnapshot.getValue(AbsenceYearly.class);

                    listAbsenceYearly.add(artist);

                    count++;
                    double progress = (100.0 * count) / total;
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                }

                Collections.sort(listAbsenceYearly);

                progressDialog.dismiss();
                rvList.setAdapter(new AbsenceYearlyListAdapter(listAbsenceYearly, mycontext));
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
