package com.example.bbbwithapi.presentation.homedetail;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bbbwithapi.R;
import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.login.LoginActivity;
import com.example.bbbwithapi.utils.IntentKeyUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DetailHomeProgramsActivity extends AppCompatActivity{
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    public static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    private ImageView ivDetailImageProgram, ivBack;
    private TextView tvTitleProgram, tvDescProgram, tvHeader;
    private String imageDrawableURL, titleProgram, descProgram;

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_home_programs);

        askPermission();
        defineIds();
        initIntent();
        initUi();
        initAction();
    }

    private void initIntent() {
        imageDrawableURL = getIntent().getStringExtra(IntentKeyUtils.keyDetailProgramImage);
        titleProgram = getIntent().getStringExtra(IntentKeyUtils.keyDetailProgramTitle);
        descProgram = getIntent().getStringExtra(IntentKeyUtils.keyDetailProgramDesc);
    }

    private void defineIds() {
        ivDetailImageProgram = (ImageView) findViewById(R.id.ivDetailHomePrograms);
        ivBack = (ImageView) findViewById(R.id.ivBackButtonToolbar);
        tvTitleProgram = (TextView) findViewById(R.id.tvTitleDetailProgramHome);
        tvDescProgram = (TextView) findViewById(R.id.tvDescDetailProgramHome);
        tvHeader = (TextView) findViewById(R.id.tvTitleToolbar);

        mycontext = this;
        prefManager = new PrefManager(mycontext);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            prefManager.removeAllPreference();
            mAuth.signOut();

            Toast.makeText(mycontext, "Waktu login Anda habis. Silakan Login kembali (1)", Toast.LENGTH_LONG).show();
            startActivity(new Intent(mycontext, LoginActivity.class));
            finish();
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
                                finish();
                            }
                        }
                    });
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mycontext);
        mFirebaseAnalytics.setUserProperty("userID", prefManager.getUserID());
        mFirebaseAnalytics.setUserProperty("userEmail", prefManager.getEmail());

        crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("userID", prefManager.getUserID());
        crashlytics.setCustomKey("userEmail", prefManager.getEmail());
    }

    private void initUi() {
        Glide.with(mycontext).load(imageDrawableURL).centerCrop().into(ivDetailImageProgram);
        tvTitleProgram.setText(titleProgram);
        tvDescProgram.setText(descProgram);
        tvHeader.setText(getString(R.string.program_details));

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initAction() {
        ivBack.setOnClickListener(view -> {
            finish();
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
