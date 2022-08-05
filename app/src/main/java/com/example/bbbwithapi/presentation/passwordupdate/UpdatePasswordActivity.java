package com.example.bbbwithapi.presentation.passwordupdate;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.preference.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import pub.devrel.easypermissions.EasyPermissions;

public class UpdatePasswordActivity extends AppCompatActivity{
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    public static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    private Button btnUpdatePassword;
    private Toolbar toolbar;
    private ImageView toolbarBackButton, ivEyePassword, ivEyeConfirmPassword, ivLogobbbUpdatePassword;
    private TextView toolbarTitle;
    private EditText etUpdatePassword, etConfirmUpdatePassword;
    private Boolean isEyePassword = true, isEyeConfirmPassword = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        askPermission();
        defineIds();
        initUi();
        initAction();
    }

    private void defineIds(){
        mAuth = FirebaseAuth.getInstance();
        etUpdatePassword = (EditText) findViewById(R.id.etUpdatePassword);
        etConfirmUpdatePassword = (EditText) findViewById(R.id.etUpdateConfirmPassword);
        btnUpdatePassword = (Button) findViewById(R.id.btnUpdatePassword);
        toolbar = (Toolbar) findViewById(R.id.toolbarWithBack);
        toolbarBackButton = (ImageView) findViewById(R.id.ivBackButtonToolbar);
        ivLogobbbUpdatePassword = (ImageView) findViewById(R.id.ivLogobbbUpdatePassword);
        ivEyePassword = (ImageView) findViewById(R.id.ivEyePasswordUpdate);
        ivEyeConfirmPassword = (ImageView) findViewById(R.id.ivEyeConfirmPasswordUpdate);
        toolbarTitle = (TextView) findViewById(R.id.tvTitleToolbar);

        prefManager = new PrefManager(this);
        mAuth = FirebaseAuth.getInstance();

        mycontext = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mycontext);
        mFirebaseAnalytics.setUserProperty("userID", prefManager.getUserID());
        mFirebaseAnalytics.setUserProperty("userEmail", prefManager.getEmail());

        crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("userID", prefManager.getUserID());
        crashlytics.setCustomKey("userEmail", prefManager.getEmail());
    }

    private void initUi() {
        toolbarTitle.setText(getString(R.string.prompt_password));

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initAction() {
        ivLogobbbUpdatePassword.setOnClickListener(view -> {
            //throw new RuntimeException("Test Crash");
        });

        ivEyePassword.setOnClickListener(view -> {
            isEyePassword = !isEyePassword;

            if (isEyePassword){
                ivEyePassword.setImageResource(R.drawable.ic_eye_disable);
                etUpdatePassword.setTransformationMethod(new PasswordTransformationMethod());
            } else {
                ivEyePassword.setImageResource(R.drawable.ic_eye_enable);
                etUpdatePassword.setTransformationMethod(new HideReturnsTransformationMethod());
            }
        });

        ivEyeConfirmPassword.setOnClickListener(view -> {
            isEyeConfirmPassword = !isEyeConfirmPassword;

            if (isEyeConfirmPassword){
                ivEyeConfirmPassword.setImageResource(R.drawable.ic_eye_disable);
                etConfirmUpdatePassword.setTransformationMethod(new PasswordTransformationMethod());
            } else {
                ivEyeConfirmPassword.setImageResource(R.drawable.ic_eye_enable);
                etConfirmUpdatePassword.setTransformationMethod(new HideReturnsTransformationMethod());
            }
        });

        btnUpdatePassword.setOnClickListener(view -> {
            String password = etUpdatePassword.getText().toString().trim();
            String confirmPassword = etConfirmUpdatePassword.getText().toString().trim();

            if (password == null || password.length() == 0 || password.equals("")) {
                Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_LONG).show();
            } else if (confirmPassword == null || confirmPassword.length() == 0 || confirmPassword.equals("")) {
                Toast.makeText(this, "Konfirmasi Password tidak boleh kosong", Toast.LENGTH_LONG).show();
            } else if (!password.equals(confirmPassword)){
                Toast.makeText(this, "Password dan Konfirmasi Password tidak sama",Toast.LENGTH_LONG).show();
            } else {
                if (mAuth.getCurrentUser() != null ) {
                    mAuth.getCurrentUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UpdatePasswordActivity.this,"Password telah diganti", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this,"Password tidak berhasil diganti karena user tidak ditemukan", Toast.LENGTH_LONG).show();
                }
            }
        });

        toolbarBackButton.setOnClickListener(view -> {
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
