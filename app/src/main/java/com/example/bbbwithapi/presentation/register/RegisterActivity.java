package com.example.bbbwithapi.presentation.register;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bbbwithapi.BBBActivity;
import com.example.bbbwithapi.R;
import com.example.bbbwithapi.helper.NumericFormatInputFilter;
import com.example.bbbwithapi.helper.PhoneNumberEditText;
import com.example.bbbwithapi.model.Class;
import com.example.bbbwithapi.model.Division;
import com.example.bbbwithapi.model.Team;
import com.example.bbbwithapi.model.UserAccount;
import com.example.bbbwithapi.preference.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

@SuppressLint("SimpleDateFormat")
@RequiresApi(api = Build.VERSION_CODES.N)
public class RegisterActivity extends AppCompatActivity{
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    public static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    public ScrollView layoutScrollMain;

    public ImageView imageULoading, ivEyePass, ivEyeConfirmPass, ivBack;
    public EditText edtUName, edtUUserName, edtUEmail, edtUPassword, edtUConfirmPassword;
    public PhoneNumberEditText edtUMobilePhone;
    private Spinner spinnerEdtDivision, spinnerEdtTeam, spinnerEdtClass;
    public Button btnUsave;
    private RadioGroup rgGender;
    private TextView tvTitle;

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    List<Division> list_division = new ArrayList<Division>();
    List<Team> list_team = new ArrayList<Team>();
    List<Class> list_class = new ArrayList<Class>();

    private Boolean isEyePassword = false, isEyeConfirmPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        askPermission();
        defineIds();
        initUi();
        initAction();
    }

    private void defineIds() {
        layoutScrollMain = (ScrollView) findViewById(R.id.layoutScrollMain);
        imageULoading = (ImageView) findViewById(R.id.imageULoading);
        ivEyePass = (ImageView) findViewById(R.id.ivEyePasswordRegister);
        ivEyeConfirmPass = (ImageView) findViewById(R.id.ivEyeConfirmPasswordRegister);
        ivBack = (ImageView) findViewById(R.id.ivBackButtonToolbar);
        edtUName = (EditText) findViewById(R.id.edtUName);
        edtUEmail = (EditText) findViewById(R.id.edtUEmail);
        edtUMobilePhone = (PhoneNumberEditText) findViewById(R.id.edtUMobilePhone);

        edtUUserName = (EditText) findViewById(R.id.edtUUserName);
        edtUUserName.setFilters(new InputFilter[] {new NumericFormatInputFilter()});

        spinnerEdtDivision = (Spinner) findViewById(R.id.spinnerEdtDivision);
        spinnerEdtTeam = (Spinner) findViewById(R.id.spinnerEdtTeam);
        spinnerEdtClass = (Spinner) findViewById(R.id.spinnerEdtClass);

        edtUPassword = (EditText) findViewById(R.id.edtUPassword);
        edtUConfirmPassword = (EditText) findViewById(R.id.edtUConfirmPassword);
        btnUsave = (Button) findViewById(R.id.btnUsave);
        rgGender = (RadioGroup) findViewById(R.id.rgSex);
        tvTitle = (TextView) findViewById(R.id.tvTitleToolbar);

        mycontext = this;
        prefManager = new PrefManager(mycontext);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mycontext);
        mFirebaseAnalytics.setUserProperty("userID", prefManager.getUserID());
        mFirebaseAnalytics.setUserProperty("userEmail", prefManager.getEmail());

        crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("userID", prefManager.getUserID());
        crashlytics.setCustomKey("userEmail", prefManager.getEmail());
    }

    private void initUi() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvTitle.setText(getString(R.string.register));

        initSpinnerDivision("");
        initSpinnerTeam("");
        initSpinnerClass("");
    }

    private void initAction() {
        btnUsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

        ivBack.setOnClickListener(view -> {
            finish();
        });

        ivEyePass.setOnClickListener(view -> {
            isEyePassword = !isEyePassword;

            if (isEyePassword) {
                ivEyePass.setImageResource(R.drawable.ic_eye_disable);
                edtUPassword.setTransformationMethod(new PasswordTransformationMethod());
            } else {
                ivEyePass.setImageResource(R.drawable.ic_eye_enable);
                edtUPassword.setTransformationMethod(new HideReturnsTransformationMethod());
            }
        });

        ivEyeConfirmPass.setOnClickListener(view -> {
            isEyeConfirmPassword = !isEyeConfirmPassword;

            if (isEyeConfirmPassword) {
                ivEyeConfirmPass.setImageResource(R.drawable.ic_eye_disable);
                edtUConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
            } else {
                ivEyeConfirmPass.setImageResource(R.drawable.ic_eye_enable);
                edtUConfirmPassword.setTransformationMethod(new HideReturnsTransformationMethod());
            }
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

    private void sendVerificationEmail() {
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            }
                        }
                    });
        }
    }

    public void saveUser() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        progressDialog.setMessage("Uploaded " + 50 + "%...");

        String name = edtUName.getText().toString().trim();
        String username = edtUUserName.getText().toString().trim();
        String email = edtUEmail.getText().toString().trim();
        String mobilephone = edtUMobilePhone.getText().toString().trim();

        Division myDivision = (Division) spinnerEdtDivision.getSelectedItem();
        Team myTeam = (Team) spinnerEdtTeam.getSelectedItem();
        Class myClass = (Class) spinnerEdtClass.getSelectedItem();

        int sex = rgGender.getCheckedRadioButtonId();
        RadioButton rbSelected = (RadioButton) findViewById(sex);

        String password = edtUPassword.getText().toString().trim();
        String confirmpassword = edtUConfirmPassword.getText().toString().trim();

        Boolean validationSuccess = true;

        if (username.equals("")) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "UserName tidak boleh kosong", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        } else if (username.indexOf(" ") >= 0) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "UserName tidak boleh ada spasi", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        } else if (name.equals("")) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "Nama tidak boleh kosong", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        } else if (mobilephone.equals("")) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "Nomor Telepon tidak boleh kosong", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        } else if (email.equals("")) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "Email tidak boleh kosong", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        } else if (myDivision == null) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "Divisi tidak boleh kosong", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        } else if (myTeam == null) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "Tim tidak boleh kosong", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        } else if (myClass == null) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "Kelas tidak boleh kosong", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        } else if (password.equals("")) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "Password tidak boleh kosong", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        } else if (confirmpassword.equals("")) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "Confirm Password tidak boleh kosong", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        } else if (!confirmpassword.equals(password)) {
            progressDialog.setMessage("Uploaded " + 100 + "%...");
            progressDialog.dismiss();
            Toast.makeText(mycontext, "Password harus sama dengan Confirm Password", Toast.LENGTH_LONG).show();
            validationSuccess = false;
        }

        if (validationSuccess) {
            String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //create user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //sign in user
                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    DatabaseReference dbUser = FirebaseDatabase.getInstance()
                                                            .getReference("user");

                                                    String myID = dbUser.push().getKey();
                                                    String myUserID = mAuth.getUid();

                                                    UserAccount myuser = new UserAccount();
                                                    myuser.setID(myID);
                                                    myuser.setUserID(myUserID);
                                                    myuser.setName(name);
                                                    myuser.setUserName(username);
                                                    myuser.setEmail(email);
                                                    myuser.setMobilePhone(mobilephone);

                                                    myuser.setDivision(myDivision.getTitle());
                                                    myuser.setTeam(myTeam.getTitle());
                                                    myuser.setUserClass(myClass.getTitle());

                                                    myuser.setSex(rbSelected.getText().toString());
                                                    myuser.setLevel(5);

                                                    myuser.setCreatedBy(myUserID);
                                                    myuser.setCreatedDate(mydate);
                                                    myuser.setIsActive(true);

                                                    dbUser
                                                            .child(mAuth.getUid())
                                                            .setValue(myuser);

                                                    prefManager.setUserID(myUserID);
                                                    prefManager.setUserName(myuser.getUserName());
                                                    prefManager.setName(myuser.getName());
                                                    prefManager.setEmail(myuser.getEmail());
                                                    prefManager.setMobilePhone(myuser.getMobilePhone());

                                                    prefManager.setDivision(myDivision.getTitle());
                                                    prefManager.setTeam(myTeam.getTitle());
                                                    prefManager.setUserClass(myClass.getTitle());

                                                    prefManager.setSex(myuser.getSex());
                                                    prefManager.setLevel(String.format("%d", myuser.getLevel()));

                                                    progressDialog.setMessage("Uploaded " + 100 + "%...");
                                                    progressDialog.dismiss();
                                                    Toast.makeText(RegisterActivity.this, "Selamat datang, " + username, Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(RegisterActivity.this, BBBActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                    sendVerificationEmail();
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    progressDialog.setMessage("Uploaded " + 100 + "%...");
                                                    progressDialog.dismiss();
                                                    Toast.makeText(RegisterActivity.this, "Initialize " + email + " failed (1)", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.setMessage("Uploaded " + 100 + "%...");
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, "Login gagal silahkan coba kembali", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        });

                            } else {
                                progressDialog.setMessage("Uploaded " + 100 + "%...");
                                progressDialog.dismiss();
                                Log.d("ErrorTagFailed","Error gan --> task "+ task.getException());
                                Toast.makeText(RegisterActivity.this, "Tidak bisa daftar menggunakan " + email, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.setMessage("Uploaded " + 100 + "%...");
                            progressDialog.dismiss();
                            Log.d("ErrorTagFailed","Error gan -->"+ e.getCause());
                            Toast.makeText(RegisterActivity.this, "Email and Password not created (2)", Toast.LENGTH_LONG).show();
                        }
                    });
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
