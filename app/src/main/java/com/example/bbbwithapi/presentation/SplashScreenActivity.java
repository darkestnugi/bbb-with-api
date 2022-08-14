package com.example.bbbwithapi.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.bbbwithapi.model.Version;
import com.example.bbbwithapi.presentation.login.LoginActivity;
import com.example.bbbwithapi.R;
import com.example.bbbwithapi.BBBActivity;
import com.example.bbbwithapi.preference.PrefManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

@RequiresApi(api = Build.VERSION_CODES.N)
@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity{
    private Context context;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        prefManager = new PrefManager(this);
        mAuth = FirebaseAuth.getInstance();

        context = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setUserProperty("userID", prefManager.getUserID());
        mFirebaseAnalytics.setUserProperty("userEmail", prefManager.getEmail());

        crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("userID", prefManager.getUserID());
        crashlytics.setCustomKey("userEmail", prefManager.getEmail());

        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        checkAppVersion();
    }

    private void checkAppVersion() {
        Query databaseVersion = FirebaseDatabase.getInstance().getReference("version").limitToLast(1000);
        databaseVersion.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalChildren = snapshot.getChildrenCount();
                String currAppVer = "";
                String thisAppVer = getString(R.string.app_version);
                Boolean isSameVersion = false;

                if (totalChildren > 0) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Version artist = postSnapshot.getValue(Version.class);

                        if (artist.getIsActive()) {
                            currAppVer = artist.getTitle();

                            if (currAppVer.equals(thisAppVer)) {
                                isSameVersion = true;
                            }
                        }
                    }

                    if (isSameVersion) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mAuth.getCurrentUser() == null) {
                                    startActivity(new Intent(context, LoginActivity.class));
                                } else {
                                    startActivity(new Intent(context, BBBActivity.class));
                                }
                                finish();
                            }
                        }, 3000);
                    } else {
                        mAuth.signOut();
                        prefManager.removeAllPreference();

                        Toast.makeText(context, "Silakan download versi terbaru " + currAppVer + ". Versi yang Anda gunakan " + thisAppVer, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
