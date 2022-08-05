package com.example.bbbwithapi.presentation.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.BBBActivity;
import com.example.bbbwithapi.model.UserAccount;
import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.passwordforget.ForgetPasswordActivity;
import com.example.bbbwithapi.presentation.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginActivity extends AppCompatActivity{
    public ActionBar actionBar;
    public Button btnUlogin;

    public ImageView imageULoading, ivEyePasswordLogin;
    public EditText edtUEmail, edtUPass;
    private TextView tvForgetPassword, tvRegister;

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    private static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;
    private Boolean isEyeClosed = true;

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        actionBar = getSupportActionBar();

        askPermission();
        defineIds();
        initUi();
        initAction();
    }

    private void initUi() {
        if (getDeviceScreenSizeInch() < 5) {
            //Do Something?
        }

        if (actionBar != null) {
            getSupportActionBar().hide();
        }

        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
    }

    private void defineIds() {
        btnUlogin = (Button) findViewById(R.id.btnUlogin);
        imageULoading = (ImageView) findViewById(R.id.imageULoading);
        ivEyePasswordLogin = (ImageView) findViewById(R.id.ivEyePasswordLogin);
        edtUEmail = (EditText) findViewById(R.id.edtUEmail);
        edtUPass = (EditText) findViewById(R.id.edtUPass);
        tvForgetPassword = (TextView) findViewById(R.id.tvForgetPassword);
        tvRegister = (TextView) findViewById(R.id.tvRegisterNow);

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

    private void initAction() {

        ivEyePasswordLogin.setOnClickListener(view -> {
            isEyeClosed = !isEyeClosed;

            if (isEyeClosed){
                ivEyePasswordLogin.setImageResource(R.drawable.ic_eye_disable);
                edtUPass.setTransformationMethod(new PasswordTransformationMethod());
            } else {
                ivEyePasswordLogin.setImageResource(R.drawable.ic_eye_enable);
                edtUPass.setTransformationMethod(new HideReturnsTransformationMethod());
            }
        });

        tvForgetPassword.setOnClickListener(view -> {
            Intent intent = new Intent(mycontext, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        btnUlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefManager.getUserName() == null) {
                    login(edtUEmail.getText().toString().trim(), edtUPass.getText().toString().trim());
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialog);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage("Do you want to logout?");
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            mAuth.signOut();
                            prefManager.removeAllPreference();

                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        tvRegister.setOnClickListener(view -> {
            Intent intent = new Intent(mycontext, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void login(String email, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        progressDialog.setMessage("Uploaded " + 50 + "%...");

        if (email.equals("")) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Please fill Email", Toast.LENGTH_LONG).show();
        }
        else if (password.equals("")) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Please fill Password", Toast.LENGTH_LONG).show();
        }
        else if (email.indexOf("@") < 0 && email.indexOf(".") < 0) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Not correct email format (1)", Toast.LENGTH_LONG).show();
        }
        else if (email.indexOf(".") == email.indexOf("@") + 1) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Not correct email format (2)", Toast.LENGTH_LONG).show();
        }
        else if (email.indexOf(" ") >= 0) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Email must not have space character", Toast.LENGTH_LONG).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (email != null && email.length() > 0) {
                                    Query databaseUser = FirebaseDatabase.getInstance().getReference("user").orderByChild("userID").equalTo(mAuth.getUid()).limitToLast(360000);
                                    databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //iterating through all the nodes
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                //getting artist
                                                UserAccount artist = postSnapshot.getValue(UserAccount.class);

                                                prefManager.setUserID(artist.getUserID());
                                                prefManager.setUserName(artist.getUserName());
                                                prefManager.setName(artist.getName());
                                                prefManager.setEmail(artist.getEmail());
                                                prefManager.setMobilePhone(artist.getMobilePhone());
                                                prefManager.setDivision(artist.getDivision());
                                                prefManager.setTeam(artist.getTeam());
                                                prefManager.setUserClass(artist.getUserClass());

                                                prefManager.setSex(artist.getSex());
                                                prefManager.setLevel(String.format("%d", artist.getLevel()));

                                                String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                                                DatabaseReference dbuser = FirebaseDatabase.getInstance()
                                                        .getReference("user");

                                                dbuser
                                                        .child(artist.getUserID())
                                                        .child("lastLogin")
                                                        .setValue(mydate);
                                            }

                                            if (prefManager.getUserName() == null) {
                                                prefManager.setUserID(mAuth.getUid());
                                                prefManager.setUserName(mAuth.getCurrentUser().getEmail());
                                                prefManager.setName(mAuth.getCurrentUser().getEmail());
                                                prefManager.setEmail(mAuth.getCurrentUser().getEmail());
                                                prefManager.setMobilePhone("-");
                                                prefManager.setSex("Laki-laki");
                                                prefManager.setLevel(String.format("%d", 1));
                                            }

                                            progressDialog.setMessage("Uploaded " + 100 + "%...");
                                            progressDialog.dismiss();

                                            Toast.makeText(LoginActivity.this, "Selamat datang, " + email, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, BBBActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            progressDialog.setMessage("Uploaded " + 100 + "%...");
                                            progressDialog.dismiss();
                                            Toast.makeText(mycontext, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            } else {
                                progressDialog.setMessage("Uploaded " + 100 + "%...");
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Email atau Password tidak ditemukan!", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.setMessage("Uploaded " + 100 + "%...");
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Error: user tidak ditemukan! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private double getDeviceScreenSizeInch() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        double wi=(double)width/(double)dm.xdpi;
        double hi=(double)height/(double)dm.ydpi;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        double screenInches = Math.sqrt(x+y);
        return screenInches;
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToSp(float dp, Context context) {
        return (int) (dpToPx(dp, context) / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int spToPx(int sp, Context context) {
        return (int) (sp * context.getResources().getDisplayMetrics().scaledDensity);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Klik 2 kali untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
