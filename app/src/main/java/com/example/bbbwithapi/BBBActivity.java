package com.example.bbbwithapi;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.account.AccountFragment;
import com.example.bbbwithapi.presentation.donation.DonationListFragment;
import com.example.bbbwithapi.presentation.home.HomeFragment;
import com.example.bbbwithapi.presentation.report.ReportFragment;
import com.example.bbbwithapi.presentation.staffadmin.StaffAdminFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

@RequiresApi(api = Build.VERSION_CODES.N)
public class BBBActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    private StaffAdminFragment staffAdminFragment;
    private HomeFragment homeFragment;
    private DonationListFragment donationListFragment;
    private AccountFragment accountFragment;
    private ReportFragment reportFragment;
    private BottomNavigationView btmNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbb);

        btmNav = (BottomNavigationView) findViewById(R.id.btmNavbbb);

        prefManager = new PrefManager(this);
        mAuth = FirebaseAuth.getInstance();

        context = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setUserProperty("userID", prefManager.getUserID());
        mFirebaseAnalytics.setUserProperty("userEmail", prefManager.getEmail());

        crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("userID", prefManager.getUserID());
        crashlytics.setCustomKey("userEmail", prefManager.getEmail());

        staffAdminFragment = new StaffAdminFragment();
        homeFragment = new HomeFragment();
        donationListFragment = new DonationListFragment();
        accountFragment = new AccountFragment();
        reportFragment = new ReportFragment();

        btmNav.setOnNavigationItemSelectedListener(this);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.flMainbbb, homeFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btmHome) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flMainbbb, homeFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.btmAdmin) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flMainbbb, staffAdminFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.btmDonation){
            getSupportFragmentManager().beginTransaction().replace(R.id.flMainbbb, donationListFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.btmReport){
            getSupportFragmentManager().beginTransaction().replace(R.id.flMainbbb, reportFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.btmAccount){
            getSupportFragmentManager().beginTransaction().replace(R.id.flMainbbb, accountFragment).commit();
            return true;
        }

        return false;
    }
}