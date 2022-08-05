package com.example.bbbwithapi.presentation.watch;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.helper.DatePickerHelper;
import com.example.bbbwithapi.model.Watch;
import com.example.bbbwithapi.model.WatchYearly;
import com.example.bbbwithapi.preference.PrefManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class WatchDetailActivity extends AppCompatActivity{
    public ScrollView layoutScrollMain;
    public Button btnUsave;

    public ImageView imageULoading, ivBackIcon;
    public TextView txtUserId, tvTitleHeader;
    public EditText edtUReferenceNumber, edtUReferenceName;
    public DatePickerHelper edtUTransactionDate;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_detail);

        askPermission();
        defineIds();
        initUI();
        initAction();
    }

    private void initUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (prefManager.getUserName() != null) {
            txtUserId.setText(prefManager.getUserID());
            edtUReferenceNumber.setText(prefManager.getUserName());
            edtUReferenceName.setText(prefManager.getName());
        }

        tvTitleHeader.setText(R.string.upload_donation_evidence);

        Calendar myCalendar = Calendar.getInstance();
        myCalendar.add(Calendar.MINUTE, -5);
        edtUTransactionDate.setMaxDate(myCalendar.getTimeInMillis());
    }

    private void defineIds() {
        layoutScrollMain = (ScrollView) findViewById(R.id.layoutScrollMain);
        imageULoading = (ImageView) findViewById(R.id.imageULoading);
        txtUserId = (TextView) findViewById(R.id.txtUserId);

        edtUTransactionDate = (DatePickerHelper) findViewById(R.id.edtUTransactionDate);
        edtUReferenceNumber = (EditText) findViewById(R.id.edtUReferenceNumber);
        edtUReferenceName = (EditText) findViewById(R.id.edtUReferenceName);

        btnUsave = (Button) findViewById(R.id.btnUsave);
        tvTitleHeader = (TextView) findViewById(R.id.tvTitleToolbar);
        ivBackIcon = (ImageView) findViewById(R.id.ivBackButtonToolbar);

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

    private void initAction() {
        ivBackIcon.setOnClickListener(view -> {
            finish();
        });

        btnUsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWatch();
            }
        });
    }

    public void saveWatch() {
        if (prefManager.getUserName() == null) {
            Toast.makeText(mycontext, "Silakan Login atau Register terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else {
            if (edtUReferenceNumber == null || edtUReferenceNumber.getText() == null || edtUReferenceNumber.getText().toString().trim().equals("") || edtUReferenceNumber.getText().toString().trim().length() == 0) {
                Toast.makeText(mycontext, "Id Relawan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (edtUReferenceName == null || edtUReferenceName.getText() == null || edtUReferenceName.getText().toString().trim().equals("") || edtUReferenceName.getText().toString().trim().length() == 0) {
                Toast.makeText(mycontext, "Nama Relawan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                String trDateDay = String.valueOf(edtUTransactionDate.getDayOfMonth());
                String trDateMonth = String.valueOf(edtUTransactionDate.getMonth() + 1);
                String trDateYear = String.valueOf(edtUTransactionDate.getYear());

                int day = edtUTransactionDate.getDayOfMonth();
                int month = edtUTransactionDate.getMonth();
                int year = edtUTransactionDate.getYear();

                DatabaseReference dbWatch = FirebaseDatabase.getInstance()
                        .getReference("watch");

                String myID = dbWatch.push().getKey();

                Watch watch = new Watch();
                watch.setID(myID);
                watch.setUserID(prefManager.getUserID());
                watch.setName(prefManager.getName());
                watch.setMobilePhone(prefManager.getMobilePhone());

                String division = prefManager.getDivision();
                if (prefManager.getDivision() == null || prefManager.getDivision().equals("") || prefManager.getDivision().length() == 0) {
                    division = "Tidak Ada";
                }

                watch.setDivision(division);

                watch.setTahunPiket(year);
                watch.setBulanPiket(month);
                watch.setTanggalPiket(day);
                watch.setUserID(prefManager.getUserID());
                watch.setCreatedBy(prefManager.getUserID());
                watch.setCreatedDate(mydate);
                watch.setIsActive(true);

                dbWatch
                        .child(myID)
                        .setValue(watch);

                Query databaseWatchYearly = FirebaseDatabase.getInstance().getReference("watchyearly").orderByChild("tahunPiket").equalTo(year).limitToLast(360000);
                databaseWatchYearly.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //clearing the previous artist list
                        int found = 0;
                        long total = dataSnapshot.getChildrenCount();

                        if (total > 0) {
                            //iterating through all the nodes
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //getting artist
                                WatchYearly artist = postSnapshot.getValue(WatchYearly.class);

                                if (year == artist.getTahunPiket() && month == artist.getBulanPiket() && day == artist.getTanggalPiket()) {
                                    ArrayList<String> listUserID = (ArrayList<String>) artist.getUserID();
                                    ArrayList<String> listName = (ArrayList<String>) artist.getName();
                                    ArrayList<String> listMobilePhone = (ArrayList<String>) artist.getMobilePhone();
                                    ArrayList<String> listDivision = (ArrayList<String>) artist.getDivision();

                                    listUserID.add(prefManager.getUserID());
                                    listName.add(prefManager.getName());
                                    listMobilePhone.add(prefManager.getMobilePhone());

                                    String division = prefManager.getDivision();
                                    if (prefManager.getDivision() == null || prefManager.getDivision().equals("") || prefManager.getDivision().length() == 0) {
                                        division = "Tidak Ada";
                                    }

                                    listDivision.add(division);

                                    artist.setUserID(listUserID);
                                    artist.setName(listName);
                                    artist.setMobilePhone(listMobilePhone);
                                    artist.setDivision(listDivision);

                                    artist.setModifiedBy(prefManager.getUserID());
                                    artist.setModifiedDate(mydate);

                                    DatabaseReference dbWatchYearly = FirebaseDatabase.getInstance()
                                            .getReference("watchyearly");

                                    dbWatchYearly
                                            .child(artist.getID())
                                            .setValue(artist);

                                    found = 1;
                                }
                            }
                        }

                        if (total == 0 || found == 0){
                            DatabaseReference dbWatchYearly = FirebaseDatabase.getInstance()
                                    .getReference("watchyearly");

                            String myID = dbWatchYearly.push().getKey();

                            WatchYearly artist = new WatchYearly();
                            ArrayList<String> listUserID = new ArrayList<>();
                            ArrayList<String> listName = new ArrayList<>();
                            ArrayList<String> listMobilePhone = new ArrayList<>();
                            ArrayList<String> listDivision = new ArrayList<>();

                            listUserID.add(prefManager.getUserID());
                            listName.add(prefManager.getName());
                            listMobilePhone.add(prefManager.getMobilePhone());

                            String division = prefManager.getDivision();
                            if (prefManager.getDivision() == null || prefManager.getDivision().equals("") || prefManager.getDivision().length() == 0) {
                                division = "Tidak Ada";
                            }

                            listDivision.add(division);

                            artist.setID(myID);
                            artist.setUserID(listUserID);
                            artist.setName(listName);
                            artist.setMobilePhone(listMobilePhone);
                            artist.setDivision(listDivision);
                            artist.setTahunPiket(year);
                            artist.setBulanPiket(month);
                            artist.setTanggalPiket(day);
                            artist.setCreatedBy(prefManager.getUserID());
                            artist.setCreatedDate(mydate);
                            artist.setModifiedBy(prefManager.getUserID());
                            artist.setModifiedDate(mydate);
                            artist.setIsActive(true);

                            dbWatchYearly
                                    .child(myID)
                                    .setValue(artist);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                Toast.makeText(mycontext, "Piket Anda telah berhasil disimpan. Kami akan segera mengkonfirmasinya.", Toast.LENGTH_SHORT).show();
                finish();
            }
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
