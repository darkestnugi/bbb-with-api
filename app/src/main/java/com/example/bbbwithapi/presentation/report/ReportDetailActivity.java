package com.example.bbbwithapi.presentation.report;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.helper.CurrencyEditText;
import com.example.bbbwithapi.helper.DatePickerHelper;
import com.example.bbbwithapi.helper.NumericFormatInputFilter;
import com.example.bbbwithapi.model.PDC;
import com.example.bbbwithapi.model.Program;
import com.example.bbbwithapi.model.ReportPDC;
import com.example.bbbwithapi.model.ReportPDCPersonal;
import com.example.bbbwithapi.preference.PrefManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ReportDetailActivity extends AppCompatActivity{
    public ScrollView layoutScrollMain;
    public Button btnUsave;

    public ImageView imageULoading, ivBackIcon;
    public TextView txtUserId, tvTitleHeader;
    public EditText edtUIntroduce, edtUFollowUp,
            edtUSuccess, edtUSuccess1Million;
    public CurrencyEditText edtUNominal;
    public Spinner spinnerEdtProgram;
    public DatePickerHelper edtUTransactionDate;

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

    List<Program> list_program = new ArrayList<Program>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

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
        }

        tvTitleHeader.setText(R.string.upload_donation_evidence);

        Calendar myCalendar = Calendar.getInstance();
        myCalendar.add(Calendar.MINUTE, -5);
        edtUTransactionDate.setMaxDate(myCalendar.getTimeInMillis());

        initSpinner("");
    }

    private void defineIds() {
        layoutScrollMain = (ScrollView) findViewById(R.id.layoutScrollMain);
        imageULoading = (ImageView) findViewById(R.id.imageULoading);

        txtUserId = (TextView) findViewById(R.id.txtUserId);

        edtUIntroduce = (EditText) findViewById(R.id.edtUIntroduce);
        edtUIntroduce.setFilters(new InputFilter[] {new NumericFormatInputFilter()});

        edtUFollowUp = (EditText) findViewById(R.id.edtUFollowUp);
        edtUFollowUp.setFilters(new InputFilter[] {new NumericFormatInputFilter()});

        edtUNominal = (CurrencyEditText) findViewById(R.id.edtUNominal);

        edtUSuccess = (EditText) findViewById(R.id.edtUSuccess);
        edtUSuccess.setFilters(new InputFilter[] {new NumericFormatInputFilter()});

        edtUSuccess1Million = (EditText) findViewById(R.id.edtUSuccess1Million);
        edtUSuccess1Million.setFilters(new InputFilter[] {new NumericFormatInputFilter()});

        edtUTransactionDate = (DatePickerHelper) findViewById(R.id.edtUTransactionDate);
        spinnerEdtProgram = (Spinner) findViewById(R.id.spinnerEdtProgram);

        btnUsave = (Button) findViewById(R.id.btnUsave);
        tvTitleHeader = (TextView) findViewById(R.id.tvTitleToolbar);
        ivBackIcon = (ImageView) findViewById(R.id.ivBackButtonToolbar);

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

    private void initAction() {
        ivBackIcon.setOnClickListener(view -> {
            finish();
        });

        btnUsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReport();
            }
        });
    }

    private void initSpinner(String myTitle) {
        DatabaseReference databaseScreen = FirebaseDatabase.getInstance().getReference("program");
        databaseScreen.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                list_program.clear();

                Program temp_program_init = new Program();
                Program curr_program = new Program();

                //temp_program_init.setID("");
                //temp_program_init.setTitle("- Pilih Jenis Program -");
                //list_program.add(temp_program_init);

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Program artist = postSnapshot.getValue(Program.class);

                    if (myTitle != null && !myTitle.equals("")){
                        if (artist.getTitle().equals(myTitle)){
                            curr_program = artist;
                        }
                    }

                    if (artist.getIsActive()) {
                        //adding artist to the list
                        list_program.add(artist);
                    }
                }

                ArrayAdapter<Program> adapter = new ArrayAdapter<Program>(mycontext, android.R.layout.simple_spinner_dropdown_item, list_program);
                spinnerEdtProgram.setAdapter(adapter);

                if (curr_program.getID() != null && !curr_program.getID().equals("")) {
                    int spinnerPosition = adapter.getPosition(curr_program);
                    spinnerEdtProgram.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mycontext, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveReport() {
        if (prefManager.getUserName() == null) {
            Toast.makeText(mycontext, "Silakan Login atau Register terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else {
            Program myProgram = (Program) spinnerEdtProgram.getSelectedItem();

            if (myProgram == null || myProgram.getTitle() == null || myProgram.getTitle().trim().equals("") || myProgram.getTitle().trim().length() == 0) {
                Toast.makeText(mycontext, "Jenis Program tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (edtUSuccess == null || edtUSuccess.getText() == null || edtUSuccess.getText().toString().trim().equals("") || edtUSuccess.getText().toString().trim().length() == 0) {
                Toast.makeText(mycontext, "Jumlah Yang Berdonasi tidak boleh kosong. Minimal isi '0'", Toast.LENGTH_SHORT).show();
            } else if (edtUSuccess1Million == null || edtUSuccess1Million.getText() == null || edtUSuccess1Million.getText().toString().trim().equals("") || edtUSuccess1Million.getText().toString().trim().length() == 0) {
                Toast.makeText(mycontext, "Jumlah Yang Berdonasi tidak boleh kosong. Minimal isi '0'", Toast.LENGTH_SHORT).show();
            } else if (edtUNominal == null || edtUNominal.getText() == null || edtUNominal.getText().toString().trim().equals("") || edtUNominal.getText().toString().trim().length() == 0) {
                Toast.makeText(mycontext, "Jumlah Nominal Donasi tidak boleh kosong. Minimal isi '0'", Toast.LENGTH_SHORT).show();
            } else {
                String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                String trDateDay = String.valueOf(edtUTransactionDate.getDayOfMonth());
                String trDateMonth = String.valueOf(edtUTransactionDate.getMonth() + 1);
                String trDateYear = String.valueOf(edtUTransactionDate.getYear());

                int day = edtUTransactionDate.getDayOfMonth();
                int month = edtUTransactionDate.getMonth();
                int year = edtUTransactionDate.getYear();

                DatabaseReference dbPDC = FirebaseDatabase.getInstance()
                        .getReference("pdc");

                String myID = dbPDC.push().getKey();

                PDC pdc = new PDC();
                pdc.setID(myID);
                pdc.setUserID(prefManager.getUserID());
                pdc.setTransactionDate(checkLength(trDateYear, 4) + "-" + checkLength(trDateMonth, 2) + "-" + checkLength(trDateDay, 2) + " 23:59:59");
                pdc.setProgramTitle(spinnerEdtProgram.getSelectedItem().toString());

                pdc.setIntroduce(Integer.parseInt("0"));
                pdc.setFollowUp(Integer.parseInt("0"));

                Double mynominal = 0.00;

                try {
                    mynominal = Double.parseDouble(edtUNominal.getText().toString().trim());
                } catch (Exception errV1) {
                    try {
                        mynominal = Double.parseDouble(edtUNominal.getText().toString().trim().replace("Rp ", "").replace(",", "").replace(".", ""));
                    } catch (Exception errV2) {
                        mynominal = 0.00;
                    }
                }

                pdc.setNominal(mynominal);

                pdc.setSuccess(Integer.parseInt(edtUSuccess.getText().toString().trim()));
                pdc.setSuccessDonorMoreThan1Million(Integer.parseInt(edtUSuccess1Million.getText().toString().trim()));

                pdc.setReferenceNumber(prefManager.getUserName());
                pdc.setReferenceName(prefManager.getName());
                pdc.setCreatedBy(prefManager.getUserID());
                pdc.setCreatedDate(mydate);
                pdc.setIsActive(true);

                pdc.setProgram(myProgram);

                dbPDC
                        .child(myID)
                        .setValue(pdc);

                Query databasePDCYearly = FirebaseDatabase.getInstance().getReference("pdcyearly").orderByChild("tahunTransaksi").equalTo(year).limitToLast(1000);
                databasePDCYearly.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //clearing the previous artist list
                        int found = 0;
                        long total = dataSnapshot.getChildrenCount();

                        if (total > 0) {
                            //iterating through all the nodes
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //getting artist
                                ReportPDC artist = postSnapshot.getValue(ReportPDC.class);

                                if (year == artist.getTahunTransaksi() && month == artist.getBulanTransaksi()) {
                                    artist.setJumlahIntroduce(artist.getJumlahIntroduce() + pdc.getIntroduce());
                                    artist.setJumlahFollowUp(artist.getJumlahFollowUp() + pdc.getFollowUp());
                                    artist.setJumlahSuccess(artist.getJumlahSuccess() + pdc.getSuccess());
                                    artist.setJumlahSuccessDonorMoreThan1Million(artist.getJumlahSuccessDonorMoreThan1Million() + pdc.getSuccessDonorMoreThan1Million());
                                    artist.setJumlahTransaksi(artist.getJumlahTransaksi() + pdc.getNominal());
                                    artist.setModifiedBy(prefManager.getUserID());
                                    artist.setModifiedDate(mydate);

                                    DatabaseReference dbPDCYearly = FirebaseDatabase.getInstance()
                                            .getReference("pdcyearly");

                                    dbPDCYearly
                                            .child(artist.getID())
                                            .setValue(artist);

                                    found = 1;
                                }
                            }
                        }

                        if (total == 0 || found == 0) {
                            DatabaseReference dbPDCYearly = FirebaseDatabase.getInstance()
                                    .getReference("pdcyearly");

                            String myID = dbPDCYearly.push().getKey();

                            ReportPDC artist = new ReportPDC();
                            artist.setID(myID);
                            artist.setTahunTransaksi(year);
                            artist.setBulanTransaksi(month);
                            artist.setJumlahIntroduce(pdc.getIntroduce());
                            artist.setJumlahFollowUp(pdc.getFollowUp());
                            artist.setJumlahSuccess(pdc.getSuccess());
                            artist.setJumlahSuccessDonorMoreThan1Million(pdc.getSuccessDonorMoreThan1Million());
                            artist.setJumlahTransaksi(pdc.getNominal());
                            artist.setCreatedBy(prefManager.getUserID());
                            artist.setCreatedDate(mydate);
                            artist.setModifiedBy(prefManager.getUserID());
                            artist.setModifiedDate(mydate);
                            artist.setIsActive(true);

                            dbPDCYearly
                                    .child(myID)
                                    .setValue(artist);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                Query databasePDCYearlyPersonal = FirebaseDatabase.getInstance().getReference("pdcyearlypersonal").orderByChild("userID").equalTo(prefManager.getUserID()).limitToLast(1000);
                databasePDCYearlyPersonal.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //clearing the previous artist list
                        int found = 0;
                        long total = dataSnapshot.getChildrenCount();

                        if (total > 0) {
                            //iterating through all the nodes
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //getting artist
                                ReportPDCPersonal artist = postSnapshot.getValue(ReportPDCPersonal.class);

                                if (year == artist.getTahunTransaksi() && month == artist.getBulanTransaksi()) {
                                    artist.setJumlahIntroduce(artist.getJumlahIntroduce() + pdc.getIntroduce());
                                    artist.setJumlahFollowUp(artist.getJumlahFollowUp() + pdc.getFollowUp());
                                    artist.setJumlahSuccess(artist.getJumlahSuccess() + pdc.getSuccess());
                                    artist.setJumlahSuccessDonorMoreThan1Million(artist.getJumlahSuccessDonorMoreThan1Million() + pdc.getSuccessDonorMoreThan1Million());
                                    artist.setJumlahTransaksi(artist.getJumlahTransaksi() + pdc.getNominal());
                                    artist.setModifiedBy(prefManager.getUserID());
                                    artist.setModifiedDate(mydate);

                                    DatabaseReference dbPDCYearlyPersonal = FirebaseDatabase.getInstance()
                                            .getReference("pdcyearlypersonal");

                                    dbPDCYearlyPersonal
                                            .child(artist.getID())
                                            .setValue(artist);

                                    found = 1;
                                }
                            }
                        }

                        if (total == 0 || found == 0) {
                            DatabaseReference dbPDCYearlyPersonal = FirebaseDatabase.getInstance()
                                    .getReference("pdcyearlypersonal");

                            String myID = dbPDCYearlyPersonal.push().getKey();

                            ReportPDCPersonal artist = new ReportPDCPersonal();
                            artist.setID(myID);
                            artist.setUserID(prefManager.getUserID());
                            artist.setTahunTransaksi(year);
                            artist.setBulanTransaksi(month);
                            artist.setJumlahIntroduce(pdc.getIntroduce());
                            artist.setJumlahFollowUp(pdc.getFollowUp());
                            artist.setJumlahSuccess(pdc.getSuccess());
                            artist.setJumlahSuccessDonorMoreThan1Million(pdc.getSuccessDonorMoreThan1Million());
                            artist.setJumlahTransaksi(pdc.getNominal());
                            artist.setCreatedBy(prefManager.getUserID());
                            artist.setCreatedDate(mydate);
                            artist.setModifiedBy(prefManager.getUserID());
                            artist.setModifiedDate(mydate);
                            artist.setIsActive(true);

                            dbPDCYearlyPersonal
                                    .child(myID)
                                    .setValue(artist);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                Toast.makeText(mycontext, "PDC Anda telah berhasil disimpan. Kami akan segera mengkonfirmasinya.", Toast.LENGTH_SHORT).show();
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

    private String checkLength(String value, int expectedLength) {
        String returnValue = "";

        if (value.length() < expectedLength) {
            expectedLength = expectedLength - value.length();

            for (int i = 0; i < expectedLength; i++) {
                if (i == 0) {
                    returnValue = "0" + value;
                } else {
                    returnValue = "0" + returnValue;
                }
            }
        } else {
            returnValue = value;
        }

        return returnValue;
    }
}
