package com.example.bbbwithapi.presentation.donation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.model.Donation;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DonationInvoiceActivity extends AppCompatActivity{
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

    TextView tvTransactionStatus, tvTransactionDate, tvAmount, tvVolunteerName, tvDonorName, tvProgramTitle;
    ImageView ivPaid;
    Button btnDownloadPDF;
    Donation mydonation;
    String dirpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_invoice);

        askPermission();
        defineIds();
        initIntent();
        initUi();
        initAction();
    }

    private void defineIds() {
        tvAmount = (TextView) findViewById(R.id.tvDonationAmount);
        tvVolunteerName = (TextView) findViewById(R.id.tvDonationVolunteerName);
        tvTransactionDate = (TextView) findViewById(R.id.tvDonationTransactionDate);
        tvTransactionStatus = (TextView) findViewById(R.id.tvDonationTransactionStatus);
        tvDonorName = (TextView) findViewById(R.id.tvDonationDonator);
        tvProgramTitle = (TextView) findViewById(R.id.tvDonationProgramTitle);
        ivPaid = (ImageView) findViewById(R.id.ivPaid);
        btnDownloadPDF = (Button) findViewById(R.id.btnDownloadPDF);

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

    private void initIntent() {
        mydonation = getIntent().getParcelableExtra(IntentKeyUtils.keyDetailDonationDetailReceipt);
    }

    @SuppressLint("DefaultLocale")
    private void initUi() {
        if (mydonation != null && mydonation.getStatusPayment().equals("Berhasil Diverifikasi")) {
            ivPaid.setVisibility(View.VISIBLE);
        } else {
            ivPaid.setVisibility(View.GONE);
        }

        tvTransactionStatus.setText(mydonation.getStatusPayment());
        tvTransactionDate.setText(mydonation.getTransactionDate().substring(0, 10));
        tvAmount.setText(String.format("Rp %,.2f", mydonation.getNominal()));
        tvVolunteerName.setText(mydonation.getReferenceName());

        if (mydonation.getDonor() != null) {
            tvDonorName.setText(mydonation.getDonor().getName());
        } else {
            tvDonorName.setText(mydonation.getDonorName());
        }

        if (mydonation.getProgram() != null) {
            tvProgramTitle.setText(mydonation.getProgram().getTitle());
        } else {
            tvProgramTitle.setText(mydonation.getProgramTitle());
        }

        btnDownloadPDF.setVisibility(View.GONE);
    }

    private void initAction() {
        btnDownloadPDF.setOnClickListener(view -> {
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.app_name),
                        600,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                layoutToImage();
            }
        });
    }

    // convert to pdf belum selesai
    public void layoutToImage() {
        ConstraintLayout clContainer = (ConstraintLayout) findViewById(R.id.clContainerInvoice);
        clContainer.setDrawingCacheEnabled(true);
        clContainer.buildDrawingCache();

        Bitmap bm = clContainer.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            imageToPDF();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("errorLayoutToImage","" + e.getMessage());
        }
    }

    public void imageToPDF() throws FileNotFoundException {
        try {
            Document document = new Document();
            dirpath = android.os.Environment.getExternalStorageDirectory().toString();
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/InvoiceBBB " + mydonation.getCreatedDate() +".pdf")); //  Change pdf's name.
            document.open();
            Image img = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scaler);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(img);
            document.close();

            Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("errorImageToPDF","" + e.getMessage());
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
