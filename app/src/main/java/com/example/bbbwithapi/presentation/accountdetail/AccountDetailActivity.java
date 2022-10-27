package com.example.bbbwithapi.presentation.accountdetail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bbbwithapi.R;
import com.example.bbbwithapi.helper.PhoneNumberEditText;
import com.example.bbbwithapi.model.Donation;
import com.example.bbbwithapi.model.Class;
import com.example.bbbwithapi.model.Division;
import com.example.bbbwithapi.model.Team;
import com.example.bbbwithapi.model.UserAccount;
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
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AccountDetailActivity extends AppCompatActivity{
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    public static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    private EditText etName, etEmail, etUsername;
    PhoneNumberEditText etPhoneNumber;
    private TextView toolbarTitle;
    private ImageView ivBack;
    private RadioGroup rgGender;
    private RadioButton rbMan, rbWoman;
    private Spinner spinnerEdtDivision, spinnerEdtTeam, spinnerEdtClass;
    private Button btnSubmitSave;

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    List<Division> list_division = new ArrayList<Division>();
    List<Team> list_team = new ArrayList<Team>();
    List<Class> list_class = new ArrayList<Class>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        askPermission();
        defineIds();
        initUi();
        initAction();
    }

    private void defineIds() {
        etName = (EditText) findViewById(R.id.etAccountDetailName);
        etEmail = (EditText) findViewById(R.id.etAccountDetailEmail);
        etPhoneNumber = (PhoneNumberEditText) findViewById(R.id.etAccountDetailPhoneNumber);
        etUsername = (EditText) findViewById(R.id.etAccountDetailUserName);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        rbMan = (RadioButton) findViewById(R.id.rbMen);
        rbWoman = (RadioButton) findViewById(R.id.rbWomen);
        btnSubmitSave = (Button) findViewById(R.id.btnSaveAccountDetail);
        toolbarTitle = (TextView) findViewById(R.id.tvTitleToolbar);
        ivBack = (ImageView) findViewById(R.id.ivBackButtonToolbar);

        spinnerEdtDivision = (Spinner) findViewById(R.id.spinnerEdtDivision);
        spinnerEdtTeam = (Spinner) findViewById(R.id.spinnerEdtTeam);
        spinnerEdtClass = (Spinner) findViewById(R.id.spinnerEdtClass);

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

                                Toast.makeText(mycontext, "Waktu login Anda habis. Silakan Login kembali (1)", Toast.LENGTH_LONG).show();
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

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initUi() {
        etName.setText(prefManager.getName());
        etEmail.setText(prefManager.getEmail());

        String mobilePhone = prefManager.getMobilePhone();
        if (mobilePhone != null && !mobilePhone.equals("") && mobilePhone.length() > 0 && mobilePhone.substring(0, 1).equals("0")) {
            mobilePhone = "62" + mobilePhone.substring(1, mobilePhone.length());
        }

        etPhoneNumber.setText(mobilePhone);
        etUsername.setText(prefManager.getUserName());
        toolbarTitle.setText("Detail Profile");

        String tempGender = "";
        if (prefManager.getSex() != null && !prefManager.getSex().equals("")) {
            tempGender = prefManager.getSex().toLowerCase();
        } else {
            tempGender = "laki - laki";
        }

        if (tempGender.equals("laki - laki") || tempGender.equals("laki-laki")) {
            rgGender.check(rbMan.getId());
        } else {
            rgGender.check(rbWoman.getId());
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initSpinnerDivision(prefManager.getDivision());
        initSpinnerTeam(prefManager.getTeam());
        initSpinnerClass(prefManager.getUserClass());
    }

    private void initAction() {
        btnSubmitSave.setOnClickListener(view -> {
            updateAccount();
        });

        ivBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void initSpinnerDivision(String myTitle) {
        Query databaseScreen = FirebaseDatabase.getInstance().getReference("division").limitToLast(1000);
        databaseScreen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                list_division.clear();

                Division temp_division_init = new Division();
                Division curr_division = new Division();

                //temp_division_init.setID("");
                //temp_division_init.setTitle("- Pilih Jenis Division -");
                //list_division.add(temp_division_init);

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Division artist = postSnapshot.getValue(Division.class);

                    if (myTitle != null && !myTitle.equals("")){
                        if (artist.getTitle().equals(myTitle)){
                            curr_division = artist;
                        }
                    }

                    if (artist.getIsActive()) {
                        //adding artist to the list
                        list_division.add(artist);
                    }
                }

                ArrayAdapter<Division> adapter = new ArrayAdapter<Division>(mycontext, android.R.layout.simple_spinner_dropdown_item, list_division);
                spinnerEdtDivision.setAdapter(adapter);

                if (curr_division.getID() != null && !curr_division.getID().equals("")) {
                    int spinnerPosition = adapter.getPosition(curr_division);
                    spinnerEdtDivision.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mycontext, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initSpinnerTeam(String myTitle) {
        Query databaseScreen = FirebaseDatabase.getInstance().getReference("team").limitToLast(1000);
        databaseScreen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                list_team.clear();

                Team temp_team_init = new Team();
                Team curr_team = new Team();

                //temp_team_init.setID("");
                //temp_team_init.setTitle("- Pilih Jenis Team -");
                //list_team.add(temp_team_init);

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Team artist = postSnapshot.getValue(Team.class);

                    if (myTitle != null && !myTitle.equals("")){
                        if (artist.getTitle().equals(myTitle)){
                            curr_team = artist;
                        }
                    }

                    if (artist.getIsActive()) {
                        //adding artist to the list
                        list_team.add(artist);
                    }
                }

                ArrayAdapter<Team> adapter = new ArrayAdapter<Team>(mycontext, android.R.layout.simple_spinner_dropdown_item, list_team);
                spinnerEdtTeam.setAdapter(adapter);

                if (curr_team.getID() != null && !curr_team.getID().equals("")) {
                    int spinnerPosition = adapter.getPosition(curr_team);
                    spinnerEdtTeam.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mycontext, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initSpinnerClass(String myTitle) {
        Query databaseScreen = FirebaseDatabase.getInstance().getReference("class").limitToLast(1000);
        databaseScreen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                list_class.clear();

                Class temp_class_init = new Class();
                Class curr_class = new Class();

                //temp_class_init.setID("");
                //temp_class_init.setTitle("- Pilih Jenis Class -");
                //list_class.add(temp_class_init);

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Class artist = postSnapshot.getValue(Class.class);

                    if (myTitle != null && !myTitle.equals("")){
                        if (artist.getTitle().equals(myTitle)){
                            curr_class = artist;
                        }
                    }

                    if (artist.getIsActive()) {
                        //adding artist to the list
                        list_class.add(artist);
                    }
                }

                ArrayAdapter<Class> adapter = new ArrayAdapter<Class>(mycontext, android.R.layout.simple_spinner_dropdown_item, list_class);
                spinnerEdtClass.setAdapter(adapter);

                if (curr_class.getID() != null && !curr_class.getID().equals("")) {
                    int spinnerPosition = adapter.getPosition(curr_class);
                    spinnerEdtClass.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mycontext, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void updateAccount() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        progressDialog.setMessage("Uploaded " + 50 + "%...");

        String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        DatabaseReference dbUserUpdate = FirebaseDatabase.getInstance().getReference("user");
        Query dbUser = FirebaseDatabase.getInstance().getReference("user").orderByChild("userID").equalTo(mAuth.getUid()).limitToLast(1);
        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserAccount dataAccount = dataSnapshot.getValue(UserAccount.class);

                    String name = etName.getText().toString().trim();
                    String username = etUsername.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String mobilePhone = etPhoneNumber.getText().toString().trim();

                    if (mobilePhone != null && !mobilePhone.equals("") && mobilePhone.length() > 0 && mobilePhone.substring(0, 1).equals("0")) {
                        mobilePhone = "+62" + mobilePhone.substring(1, mobilePhone.length());
                    }

                    if (name.equals("")) {
                        progressDialog.setMessage("Uploaded " + 100 + "%...");
                        progressDialog.dismiss();
                        Toast.makeText(mycontext, "Nama tidak boleh kosong", Toast.LENGTH_LONG).show();
                    } else if (username.equals("")) {
                        progressDialog.setMessage("Uploaded " + 100 + "%...");
                        progressDialog.dismiss();
                        Toast.makeText(mycontext, "ID Relawan tidak boleh kosong", Toast.LENGTH_LONG).show();
                    } else if (email.equals("")) {
                        progressDialog.setMessage("Uploaded " + 100 + "%...");
                        progressDialog.dismiss();
                        Toast.makeText(mycontext, "Email tidak boleh kosong", Toast.LENGTH_LONG).show();
                    } else if (mobilePhone.equals("")) {
                        progressDialog.setMessage("Uploaded " + 100 + "%...");
                        progressDialog.dismiss();
                        Toast.makeText(mycontext, "Nomor Telepon tidak boleh kosong", Toast.LENGTH_LONG).show();
                    } else {
                        if (dataAccount != null) {
                            Division myDivision = (Division) spinnerEdtDivision.getSelectedItem();
                            Team myTeam = (Team) spinnerEdtTeam.getSelectedItem();
                            Class myClass = (Class) spinnerEdtClass.getSelectedItem();

                            dataAccount.setName(name);
                            dataAccount.setUserName(username);
                            dataAccount.setEmail(email);
                            dataAccount.setMobilePhone(mobilePhone);

                            dataAccount.setSex(((RadioButton) findViewById(rgGender.getCheckedRadioButtonId())).getText().toString());
                            dataAccount.setDivision(myDivision.getTitle());
                            dataAccount.setTeam(myTeam.getTitle());
                            dataAccount.setUserClass(myClass.getTitle());
                            dataAccount.setModifiedDate(dateNow);
                            dataAccount.setModifiedBy(prefManager.getUserID());

                            String tempGender = dataAccount.getSex().toLowerCase().trim();

                            if (tempGender.equals("laki - laki") || tempGender.equals("laki-laki")) {
                                rgGender.check(rbMan.getId());
                            } else {
                                rgGender.check(rbWoman.getId());
                            }

                            prefManager.setName(name);
                            prefManager.setUserName(username);
                            prefManager.setEmail(email);
                            prefManager.setMobilePhone(mobilePhone);

                            prefManager.setLevel(String.valueOf(dataAccount.getLevel()));
                            prefManager.setUserID(dataAccount.getUserID());
                            prefManager.setDivision(myDivision.getTitle());
                            prefManager.setTeam(myTeam.getTitle());
                            prefManager.setUserClass(myClass.getTitle());

                            dbUserUpdate
                                    .child(prefManager.getUserID())
                                    .setValue(dataAccount);

                            updateDonation(username, name, myDivision.getTitle(), myTeam.getTitle(), myClass.getTitle());

                            progressDialog.setMessage("Uploaded " + 100 + "%...");
                            progressDialog.dismiss();

                            Toast.makeText(mycontext, "Berhasil mengubah data pribadi", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mycontext, "Internet tidak stabil. Mohon periksa kembali jaringan internet Anda", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void updateDonation(String refNumber, String refName, String refDivision, String refTeam, String refClass) {
        Query databaseDonation = FirebaseDatabase.getInstance().getReference("donation").orderByChild("userID").equalTo(prefManager.getUserID()).limitToLast(360000);
        databaseDonation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                long total = dataSnapshot.getChildrenCount();

                if (total > 0) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        Donation artist = postSnapshot.getValue(Donation.class);

                        artist.setReferenceNumber(refNumber);
                        artist.setReferenceName(refName);
                        artist.setReferenceDivision(refDivision);
                        artist.setReferenceTeam(refTeam);
                        artist.setReferenceClass(refClass);

                        DatabaseReference dbDonation = FirebaseDatabase.getInstance()
                                .getReference("donation");

                        dbDonation
                                .child(artist.getID())
                                .setValue(artist);
                    }

                    Toast.makeText(AccountDetailActivity.this, "Berhasil mengubah data donasi", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AccountDetailActivity.this, "Data donasi kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
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
