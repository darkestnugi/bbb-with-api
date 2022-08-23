package com.example.bbbwithapi;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.account.AccountFragment;
import com.example.bbbwithapi.presentation.donation.DonationListFragment;
import com.example.bbbwithapi.presentation.home.HomeFragment;
import com.example.bbbwithapi.presentation.login.LoginActivity;
import com.example.bbbwithapi.presentation.report.ReportFragment;
import com.example.bbbwithapi.presentation.staffadmin.StaffAdminFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

@RequiresApi(api = Build.VERSION_CODES.N)
public class BBBActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
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

        mycontext = this;
        prefManager = new PrefManager(this);
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