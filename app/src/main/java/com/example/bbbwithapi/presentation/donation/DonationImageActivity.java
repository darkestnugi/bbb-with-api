package com.example.bbbwithapi.presentation.donation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.bbbwithapi.R;
import com.example.bbbwithapi.helper.ZoomableImageView;
import com.example.bbbwithapi.model.Donation;
import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.login.LoginActivity;
import com.example.bbbwithapi.remote.BitMapTransform;
import com.example.bbbwithapi.utils.IntentKeyUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DonationImageActivity extends AppCompatActivity {
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    public static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;

    private int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
    private int maxDownloadSize = MAX_WIDTH * MAX_HEIGHT;

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    private Picasso myotherpicasso;
    private ImageView ivPhotoLarge, ivBack;
    private TextView tvHeader;
    private Donation mydonation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_image);

        askPermission();
        defineIds();
        initIntent();
        initUi();
        initAction();
    }

    private void defineIds() {
        ivPhotoLarge = (ImageView) findViewById(R.id.ivPhotoLarge);
        ivBack = (ImageView) findViewById(R.id.ivBackButtonToolbar);
        tvHeader = (TextView) findViewById(R.id.tvTitleToolbar);

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

        //myotherpicasso = new Picasso.Builder(this)
        //        .listener(new Picasso.Listener() {
        //            @Override
        //            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
        //                Toast.makeText(mycontext, "Load File Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        //            }
        //        }).build();
    }

    private void initIntent() {
        mydonation = getIntent().getParcelableExtra(IntentKeyUtils.keyDetailDonationDetailImage);
    }

    @SuppressLint("DefaultLocale")
    private void initUi() {
        if (mydonation != null) {
            Glide.with(mycontext).load(mydonation.getPhotoURL()).into(ivPhotoLarge);

            //myotherpicasso
            //        .load(mydonation.getPhotoURL())
            //.memoryPolicy(MemoryPolicy.NO_CACHE)
            //.networkPolicy(NetworkPolicy.NO_CACHE)
            //        .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
            //        .resize(size, size)
            //        .centerInside()
            //        .into(ivPhotoLarge);

            tvHeader.setText(getString(R.string.photo_details));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initAction() {
        ivBack.setOnClickListener(view -> {
            finish();
        });

        ivPhotoLarge.setOnClickListener(view -> {
            final String usrid = mydonation.getUserID();
            final String filename = mydonation.getPhoto();

            if (filename != null && !filename.equals("")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("Donation/" + usrid + "/" + filename);
                storageReference.getBytes(maxDownloadSize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        InputStream inputStream = new ByteArrayInputStream(bytes);

                        String writtenToDisk = writeResponseBodyToDisk(filename, inputStream);
                        Toast.makeText(mycontext, writtenToDisk, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(mycontext, "File downloaded failed. " + exception.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public String writeResponseBodyToDisk(String filename, InputStream inputStream){
        try {
            boolean success = true;
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/VisiMahaKarya/";
            File folder = new File(directory);
            if (!folder.exists()){
                success = folder.mkdir();
            }

            if (success) {
                File futureStudioIconFile = new File(directory + filename);
                OutputStream outputStream = null;

                try {
                    byte[] fileReader = new byte[4096];
                    long fileSizeDownloaded = 0;
                    outputStream = new FileOutputStream(futureStudioIconFile);

                    while (true) {
                        int read = inputStream.read(fileReader);

                        if (read == -1) {
                            break;
                        }

                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                    }

                    outputStream.flush();

                    return "Berhasil disimpan di :" + directory + filename;
                } catch (IOException e) {
                    return e.getMessage();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }
            else{
                return "Directory Penyimpanan gagal dibuat";
            }
        } catch (IOException e) {
            return e.getMessage();
        }
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