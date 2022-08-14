package com.example.bbbwithapi.presentation.donation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.bbbwithapi.R;
import com.example.bbbwithapi.helper.CurrencyEditText;
import com.example.bbbwithapi.helper.DatePickerHelper;
import com.example.bbbwithapi.helper.PhoneNumberEditText;
import com.example.bbbwithapi.model.Age;
import com.example.bbbwithapi.model.Category;
import com.example.bbbwithapi.model.Domicile;
import com.example.bbbwithapi.model.Donation;
import com.example.bbbwithapi.model.Donor;
import com.example.bbbwithapi.model.Job;
import com.example.bbbwithapi.model.Program;
import com.example.bbbwithapi.model.Religion;
import com.example.bbbwithapi.model.Report;
import com.example.bbbwithapi.model.ReportPersonal;
import com.example.bbbwithapi.model.UserAccount;
import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.remote.BitMapTransform;
import com.example.bbbwithapi.remote.FileUtils;
import com.example.bbbwithapi.utils.IntentKeyUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DonationDetailActivity extends AppCompatActivity{
    public ScrollView layoutScrollMain;
    public Button btnUsave;

    public ImageView imageULoading, imageUDownload, ivBackIcon;
    public TextView txtDonationId, txtUserId, txtPhoto, txtPhotoURL, tvTitleHeader;
    public EditText edtUEmail, edtUName, edtUPrayer, edtUReferenceNumber, edtUReferenceName;
    public PhoneNumberEditText edtUMobilePhone;
    public CurrencyEditText edtUNominal;
    public Spinner spinnerEdtProgram, spinnerEdtReligion, spinnerEdtJob, spinnerEdtAge, spinnerEdtDomicile, spinnerEdtCategory;
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
    private Picasso myotherpicasso;

    private static final int PICK_IMAGE = 1;
    private static final int SELECT_PICK = 1;

    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;

    private int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
    private int maxDownloadSize = MAX_WIDTH * MAX_HEIGHT;

    private static final String imageUrl = "https://via.placeholder.com/500";

    private Uri fileUri;
    private String filePath;

    private Donation mydonation;
    private boolean isInsert = true;

    List<Program> list_program = new ArrayList<Program>();
    List<Religion> list_religion = new ArrayList<Religion>();
    List<Job> list_job = new ArrayList<Job>();
    List<Age> list_age = new ArrayList<Age>();
    List<Domicile> list_domicile = new ArrayList<Domicile>();
    List<Category> list_category = new ArrayList<Category>();

    private String fileName, fileId, fileExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        askPermission();
        defineIds();
        initIntent();
        initUI();
        initAction();
    }

    private void initIntent() {
        mydonation = getIntent().getParcelableExtra(IntentKeyUtils.keyDetailDonationDetailData);
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

        myotherpicasso = new Picasso.Builder(mycontext)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Toast.makeText(mycontext, "Load File Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).build();

        tvTitleHeader.setText(R.string.upload_donation_evidence);

        Calendar myCalendar = Calendar.getInstance();
        myCalendar.add(Calendar.MINUTE, -5);
        edtUTransactionDate.setMaxDate(myCalendar.getTimeInMillis());

        if (mydonation != null) {
            isInsert = false;
            txtDonationId.setText(mydonation.getID());
            txtUserId.setText(mydonation.getUserID());

            edtUName.setText(mydonation.getDonorName());
            edtUEmail.setText(mydonation.getDonorEmail());
            edtUMobilePhone.setText(mydonation.getDonorMobilePhone());

            //format nilai rupiah
            edtUNominal.setText(String.format("Rp %,.0f", mydonation.getNominal()).replace(".", ","));

            edtUPrayer.setText(mydonation.getPrayer());
            edtUReferenceNumber.setText(mydonation.getReferenceNumber());
            edtUReferenceName.setText(mydonation.getReferenceName());

            txtPhoto.setText(mydonation.getPhoto());
            txtPhotoURL.setText(mydonation.getPhotoURL());
            Glide.with(mycontext).load(mydonation.getPhotoURL()).into(imageUDownload);

            initSpinnerProgram(mydonation.getProgramTitle());
            initSpinnerReligion(mydonation.getReligionTitle());
            initSpinnerJob(mydonation.getJobTitle());
            initSpinnerAge(mydonation.getAgeTitle());
            initSpinnerDomicile(mydonation.getDomicileTitle());
            initSpinnerCategory(mydonation.getCategoryTitle());
        } else {
            initSpinnerProgram("");
            initSpinnerReligion("");
            initSpinnerJob("");
            initSpinnerAge("");
            initSpinnerDomicile("");
            initSpinnerCategory("");
        }
    }

    private void defineIds() {
        layoutScrollMain = (ScrollView) findViewById(R.id.layoutScrollMain);
        imageULoading = (ImageView) findViewById(R.id.imageULoading);
        imageUDownload = (ImageView) findViewById(R.id.imageUDownload);

        txtDonationId = (TextView) findViewById(R.id.txtDonationId);
        txtUserId = (TextView) findViewById(R.id.txtUserId);
        edtUEmail = (EditText) findViewById(R.id.edtUEmail);
        edtUMobilePhone = (PhoneNumberEditText) findViewById(R.id.edtUMobilePhone);
        edtUName = (EditText) findViewById(R.id.edtUName);

        edtUNominal = (CurrencyEditText) findViewById(R.id.edtUNominal);
        edtUPrayer = (EditText) findViewById(R.id.edtUPrayer);
        edtUReferenceNumber = (EditText) findViewById(R.id.edtUReferenceNumber);
        edtUReferenceName = (EditText) findViewById(R.id.edtUReferenceName);

        edtUTransactionDate = (DatePickerHelper) findViewById(R.id.edtUTransactionDate);
        spinnerEdtProgram = (Spinner) findViewById(R.id.spinnerEdtProgram);
        spinnerEdtReligion = (Spinner) findViewById(R.id.spinnerEdtReligion);
        spinnerEdtJob = (Spinner) findViewById(R.id.spinnerEdtJob);
        spinnerEdtAge = (Spinner) findViewById(R.id.spinnerEdtAge);
        spinnerEdtDomicile = (Spinner) findViewById(R.id.spinnerEdtDomicile);
        spinnerEdtCategory = (Spinner) findViewById(R.id.spinnerEdtCategory);

        txtPhoto = (TextView) findViewById(R.id.txtPhoto);
        txtPhotoURL = (TextView) findViewById(R.id.txtPhotoURL);
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
                if (fileUri == null && txtPhotoURL.getText() != null && txtPhotoURL.getText().length() > 0 && !txtPhotoURL.getText().equals("")) {
                    saveDonation();
                } else if (fileUri != null) {
                    uploadFile(fileId, fileName, fileExtension, fileUri);
                } else {
                    Toast.makeText(DonationDetailActivity.this, "Gambar tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

        imageUDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);

                Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
                intent.setDataAndType(uri, "image/*");

                startActivityForResult(intent, SELECT_PICK);
            }
        });
    }

    private void initSpinnerProgram(String myTitle) {
        Query databaseScreen = FirebaseDatabase.getInstance().getReference("program").limitToLast(360000);
        databaseScreen.addValueEventListener(new ValueEventListener() {
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

    private void initSpinnerReligion(String myTitle) {
        Query databaseScreen = FirebaseDatabase.getInstance().getReference("religion").limitToLast(360000);
        databaseScreen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                list_religion.clear();

                Religion temp_religion_init = new Religion();
                Religion curr_religion = new Religion();

                //temp_religion_init.setID("");
                //temp_religion_init.setTitle("- Pilih Jenis Religion -");
                //list_religion.add(temp_religion_init);

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Religion artist = postSnapshot.getValue(Religion.class);

                    if (myTitle != null && !myTitle.equals("")){
                        if (artist.getTitle().equals(myTitle)){
                            curr_religion = artist;
                        }
                    }

                    if (artist.getIsActive()) {
                        //adding artist to the list
                        list_religion.add(artist);
                    }
                }

                ArrayAdapter<Religion> adapter = new ArrayAdapter<Religion>(mycontext, android.R.layout.simple_spinner_dropdown_item, list_religion);
                spinnerEdtReligion.setAdapter(adapter);

                if (curr_religion.getID() != null && !curr_religion.getID().equals("")) {
                    int spinnerPosition = adapter.getPosition(curr_religion);
                    spinnerEdtReligion.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mycontext, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initSpinnerJob(String myTitle) {
        Query databaseScreen = FirebaseDatabase.getInstance().getReference("job").limitToLast(360000);
        databaseScreen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                list_job.clear();

                Job temp_job_init = new Job();
                Job curr_job = new Job();

                //temp_job_init.setID("");
                //temp_job_init.setTitle("- Pilih Jenis Job -");
                //list_job.add(temp_job_init);

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Job artist = postSnapshot.getValue(Job.class);

                    if (myTitle != null && !myTitle.equals("")){
                        if (artist.getTitle().equals(myTitle)){
                            curr_job = artist;
                        }
                    }

                    if (artist.getIsActive()) {
                        //adding artist to the list
                        list_job.add(artist);
                    }
                }

                ArrayAdapter<Job> adapter = new ArrayAdapter<Job>(mycontext, android.R.layout.simple_spinner_dropdown_item, list_job);
                spinnerEdtJob.setAdapter(adapter);

                if (curr_job.getID() != null && !curr_job.getID().equals("")) {
                    int spinnerPosition = adapter.getPosition(curr_job);
                    spinnerEdtJob.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mycontext, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initSpinnerAge(String myTitle) {
        Query databaseScreen = FirebaseDatabase.getInstance().getReference("age").limitToLast(360000);
        databaseScreen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                list_age.clear();

                Age temp_age_init = new Age();
                Age curr_age = new Age();

                //temp_age_init.setID("");
                //temp_age_init.setTitle("- Pilih Jenis Age -");
                //list_age.add(temp_age_init);

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Age artist = postSnapshot.getValue(Age.class);

                    if (myTitle != null && !myTitle.equals("")){
                        if (artist.getTitle().equals(myTitle)){
                            curr_age = artist;
                        }
                    }

                    if (artist.getIsActive()) {
                        //adding artist to the list
                        list_age.add(artist);
                    }
                }

                ArrayAdapter<Age> adapter = new ArrayAdapter<Age>(mycontext, android.R.layout.simple_spinner_dropdown_item, list_age);
                spinnerEdtAge.setAdapter(adapter);

                if (curr_age.getID() != null && !curr_age.getID().equals("")) {
                    int spinnerPosition = adapter.getPosition(curr_age);
                    spinnerEdtAge.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mycontext, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initSpinnerDomicile(String myTitle) {
        Query databaseScreen = FirebaseDatabase.getInstance().getReference("domicile").limitToLast(360000);
        databaseScreen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                list_domicile.clear();

                Domicile temp_domicile_init = new Domicile();
                Domicile curr_domicile = new Domicile();

                //temp_domicile_init.setID("");
                //temp_domicile_init.setTitle("- Pilih Jenis Domicile -");
                //list_domicile.add(temp_domicile_init);

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Domicile artist = postSnapshot.getValue(Domicile.class);

                    if (myTitle != null && !myTitle.equals("")){
                        if (artist.getTitle().equals(myTitle)){
                            curr_domicile = artist;
                        }
                    }

                    if (artist.getIsActive()) {
                        //adding artist to the list
                        list_domicile.add(artist);
                    }
                }

                ArrayAdapter<Domicile> adapter = new ArrayAdapter<Domicile>(mycontext, android.R.layout.simple_spinner_dropdown_item, list_domicile);
                spinnerEdtDomicile.setAdapter(adapter);

                if (curr_domicile.getID() != null && !curr_domicile.getID().equals("")) {
                    int spinnerPosition = adapter.getPosition(curr_domicile);
                    spinnerEdtDomicile.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mycontext, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initSpinnerCategory(String myTitle) {
        Query databaseScreen = FirebaseDatabase.getInstance().getReference("category").limitToLast(360000);
        databaseScreen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                list_category.clear();

                Category temp_category_init = new Category();
                Category curr_category = new Category();

                //temp_category_init.setID("");
                //temp_category_init.setTitle("- Pilih Jenis Category -");
                //list_category.add(temp_category_init);

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Category artist = postSnapshot.getValue(Category.class);

                    if (myTitle != null && !myTitle.equals("")){
                        if (artist.getTitle().equals(myTitle)){
                            curr_category = artist;
                        }
                    }

                    if (artist.getIsActive()) {
                        //adding artist to the list
                        list_category.add(artist);
                    }
                }

                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(mycontext, android.R.layout.simple_spinner_dropdown_item, list_category);
                spinnerEdtCategory.setAdapter(adapter);

                if (curr_category.getID() != null && !curr_category.getID().equals("")) {
                    int spinnerPosition = adapter.getPosition(curr_category);
                    spinnerEdtCategory.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mycontext, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == PICK_IMAGE) {
            fileUri = data.getData();
            File file = FileUtils.getFile(this, fileUri);

            if (fileUri != null && "content".equals(fileUri.getScheme())) {
                Cursor cursor = this.getContentResolver().query(fileUri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                filePath = fileUri.getPath();
            }

            String fileId = txtUserId.getText().toString().trim();
            if (file != null && fileId != null && fileId.length() > 0) {
                String filename = (String) DateFormat.format("yyyyMMdd_hhmmss_a", new Date());
                String extension = (file.getName()).substring((file.getName()).lastIndexOf("."));

                this.fileId = fileId;
                this.fileName = filename;
                this.fileExtension = extension;

                imageUDownload.setImageBitmap(BitmapFactory.decodeFile(filePath));

                txtPhoto.setText(filename + extension);
            } else {
                Toast.makeText(mycontext, "file id cannot empty!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean uploadFile(String fileId, String filename, String extension, Uri fileUri) {
        boolean result = true;

        if (prefManager.getUserName() == null) {
            Toast.makeText(mycontext, "Silakan Login atau Register terlebih dahulu", Toast.LENGTH_SHORT).show();
            result = false;
        } else {
            File file = FileUtils.getFile(this, fileUri);

            if ((file.length() / 1024) >= 2048) {
                Toast.makeText(mycontext, "Error: maximum file size is 2048 Kb. Current size " + (file.length() / 1024) + " Kb", Toast.LENGTH_SHORT).show();
                result = false;
            } else {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();

                Query databaseDonation = FirebaseDatabase.getInstance().getReference("donation").limitToLast(360000);
                databaseDonation.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //clearing the previous artist list
                        long total = dataSnapshot.getChildrenCount();

                        String myphone = edtUMobilePhone.getText().toString().trim();

                        if (myphone != null && !myphone.equals("") && myphone.length() > 0 && myphone.substring(0, 1).equals("0")) {
                            myphone = "62" + myphone.substring(1, myphone.length());
                        }

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

                        String myUserId = txtUserId.getText().toString().trim();

                        String trDateDay = String.valueOf(edtUTransactionDate.getDayOfMonth());
                        String trDateMonth = String.valueOf(edtUTransactionDate.getMonth() + 1);
                        String trDateYear = String.valueOf(edtUTransactionDate.getYear());

                        String selectedDate = checkLength(trDateYear, 4) + "-" + checkLength(trDateMonth, 2) + "-" + checkLength(trDateDay, 2);
                        Boolean isSaved = true;

                        if (total > 0) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //getting artist
                                Donation artist = postSnapshot.getValue(Donation.class);
                                String temp1 = artist.getDonor().getMobilePhone();
                                Double temp2 = artist.getNominal();
                                String temp3 = artist.getTransactionDate().substring(0, 10);
                                String temp4 = artist.getCreatedBy();

                                Boolean v1 = myphone.equals(temp1);
                                Boolean v2 = mynominal.equals(temp2);
                                Boolean v3 = selectedDate.equals(temp3);
                                Boolean v4 = myUserId.equals(temp4);

                                if (v1 && v2 && v3 && v4) {
                                    isSaved = false;
                                    Toast.makeText(mycontext, "Donasi donatur ini sudah pernah diupload oleh Anda pada tanggal " + selectedDate, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    break;
                                } else if (v1 && v2 && v3 && !v4) {
                                    isSaved = false;
                                    Toast.makeText(mycontext, "Donasi donatur ini sudah pernah diupload oleh relawan lain pada tanggal " + selectedDate, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    break;
                                }
                            }

                            if (isSaved) {
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference("Donation/" + fileId + "/" + filename + extension);
                                storageReference.putFile(fileUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.d("successUpload", taskSnapshot.getMetadata().toString());
                                                progressDialog.dismiss();

                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri downloadUrl) {
                                                        Log.d("successGetDownloadURl", downloadUrl.toString());
                                                        myotherpicasso
                                                                .load(downloadUrl)
                                                                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                                                //.networkPolicy(NetworkPolicy.NO_CACHE)
                                                                .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                                                .resize(size, size)
                                                                .centerInside()
                                                                .into(imageUDownload);

                                                        txtPhoto.setText(filename + extension);
                                                        txtPhotoURL.setText(downloadUrl.toString());

                                                        saveDonation();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Log.d("failGetDownloadURl", exception.toString());
                                                        myotherpicasso
                                                                .load(imageUrl)
                                                                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                                                //.networkPolicy(NetworkPolicy.NO_CACHE)
                                                                .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                                                .resize(size, size)
                                                                .centerInside()
                                                                .into(imageUDownload);

                                                        txtPhoto.setText(filename + extension);
                                                        txtPhotoURL.setText(imageUrl);
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Log.d("uploadFail", exception.toString());
                                                progressDialog.dismiss();

                                                txtPhoto.setText(filename + extension);
                                                txtPhotoURL.setText(imageUrl);
                                            }
                                        })
                                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                                            }
                                        });
                            }
                        } else {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Donation/" + fileId + "/" + filename + extension);
                            storageReference.putFile(fileUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Log.d("successUpload", taskSnapshot.getMetadata().toString());
                                            progressDialog.dismiss();

                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri downloadUrl) {
                                                    Log.d("successGetDownloadURl", downloadUrl.toString());
                                                    myotherpicasso
                                                            .load(downloadUrl)
                                                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                                            //.networkPolicy(NetworkPolicy.NO_CACHE)
                                                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                                            .resize(size, size)
                                                            .centerInside()
                                                            .into(imageUDownload);

                                                    txtPhoto.setText(filename + extension);
                                                    txtPhotoURL.setText(downloadUrl.toString());

                                                    saveDonation();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Log.d("failGetDownloadURl", exception.toString());
                                                    myotherpicasso
                                                            .load(imageUrl)
                                                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                                            //.networkPolicy(NetworkPolicy.NO_CACHE)
                                                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                                            .resize(size, size)
                                                            .centerInside()
                                                            .into(imageUDownload);

                                                    txtPhoto.setText(filename + extension);
                                                    txtPhotoURL.setText(imageUrl);
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Log.d("uploadFail", exception.toString());
                                            progressDialog.dismiss();

                                            txtPhoto.setText(filename + extension);
                                            txtPhotoURL.setText(imageUrl);
                                        }
                                    })
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }

        return result;
    }

    public void saveDonation() {
        String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        if (prefManager.getUserName() == null) {
            Toast.makeText(mycontext, "Silakan Login atau Register terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else {
            Program myProgram = (Program) spinnerEdtProgram.getSelectedItem();
            Religion myReligion = (Religion) spinnerEdtReligion.getSelectedItem();
            Job myJob = (Job) spinnerEdtJob.getSelectedItem();
            Age myAge = (Age) spinnerEdtAge.getSelectedItem();
            Domicile myDomicile = (Domicile) spinnerEdtDomicile.getSelectedItem();
            Category myCategory = (Category) spinnerEdtCategory.getSelectedItem();

            if (edtUName == null || edtUName.getText() == null || edtUName.getText().toString().trim().equals("") || edtUName.getText().toString().trim().length() == 0) {
                Toast.makeText(mycontext, "Nama Donatur tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (edtUMobilePhone == null || edtUMobilePhone.getText() == null || edtUMobilePhone.getText().toString().trim().equals("") || edtUMobilePhone.getText().toString().trim().length() == 0) {
                Toast.makeText(mycontext, "Nomor Telepon Donatur tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (myProgram == null || myProgram.getTitle() == null || myProgram.getTitle().trim().equals("") || myProgram.getTitle().trim().length() == 0) {
                Toast.makeText(mycontext, "Jenis Program tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (myReligion == null || myReligion.getTitle() == null || myReligion.getTitle().trim().equals("") || myReligion.getTitle().trim().length() == 0) {
                Toast.makeText(mycontext, "Agama Donatur tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (myJob == null || myJob.getTitle() == null || myJob.getTitle().trim().equals("") || myJob.getTitle().trim().length() == 0) {
                Toast.makeText(mycontext, "Pekerjaan Donatur tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (myAge == null || myAge.getTitle() == null || myAge.getTitle().trim().equals("") || myAge.getTitle().trim().length() == 0) {
                Toast.makeText(mycontext, "Umur Donatur tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (myDomicile == null || myDomicile.getTitle() == null || myDomicile.getTitle().trim().equals("") || myDomicile.getTitle().trim().length() == 0) {
                Toast.makeText(mycontext, "Domisili Donatur tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (myCategory == null || myCategory.getTitle() == null || myCategory.getTitle().trim().equals("") || myCategory.getTitle().trim().length() == 0) {
                Toast.makeText(mycontext, "Jenis Donatur tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (edtUReferenceNumber == null || edtUReferenceNumber.getText() == null || edtUReferenceNumber.getText().toString().trim().equals("") || edtUReferenceNumber.getText().toString().trim().length() == 0) {
                Toast.makeText(mycontext, "Id Relawan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (edtUReferenceName == null || edtUReferenceName.getText() == null || edtUReferenceName.getText().toString().trim().equals("") || edtUReferenceName.getText().toString().trim().length() == 0) {
                Toast.makeText(mycontext, "Nama Relawan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (edtUNominal == null || edtUNominal.getText() == null || edtUNominal.getText().toString().trim().equals("") || edtUNominal.getText().toString().trim().length() == 0) {
                Toast.makeText(mycontext, "Nominal tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if (Double.parseDouble(edtUNominal.getText().toString().replace("Rp", "").replace(",","").replace(".","").trim()) <= 0) {
                Toast.makeText(mycontext, "Nominal tidak boleh kecil atau sama dengan nol", Toast.LENGTH_SHORT).show();
            } else {
                DatabaseReference dbDonation = FirebaseDatabase.getInstance()
                        .getReference("donation");

                String myID = dbDonation.push().getKey();
                if (!isInsert) {
                    myID = mydonation.getID();
                }

                String trDateDay = String.valueOf(edtUTransactionDate.getDayOfMonth());
                String trDateMonth = String.valueOf(edtUTransactionDate.getMonth() + 1);
                String trDateYear = String.valueOf(edtUTransactionDate.getYear());

                int day = edtUTransactionDate.getDayOfMonth();
                int month = edtUTransactionDate.getMonth();
                int year = edtUTransactionDate.getYear();

                Donation donation = new Donation();
                donation.setID(myID);
                donation.setUserID(prefManager.getUserID());
                donation.setTransactionDate(checkLength(trDateYear, 4) + "-" + checkLength(trDateMonth, 2) + "-" + checkLength(trDateDay, 2) + " 23:59:59");

                donation.setDonorName(edtUName.getText().toString());
                donation.setDonorEmail(edtUEmail.getText().toString());
                donation.setDonorMobilePhone(edtUMobilePhone.getText().toString());

                donation.setProgramTitle(spinnerEdtProgram.getSelectedItem().toString());
                donation.setReligionTitle(spinnerEdtReligion.getSelectedItem().toString());
                donation.setJobTitle(spinnerEdtJob.getSelectedItem().toString());
                donation.setAgeTitle(spinnerEdtAge.getSelectedItem().toString());
                donation.setDomicileTitle(spinnerEdtDomicile.getSelectedItem().toString());
                donation.setCategoryTitle(spinnerEdtCategory.getSelectedItem().toString());

                donation.setNominal(Double.parseDouble(edtUNominal.getText().toString().replace("Rp", "").replace(",","").replace(".","").trim()));
                donation.setPrayer(edtUPrayer.getText().toString().trim());

                donation.setReferenceNumber(edtUReferenceNumber.getText().toString().trim());
                donation.setReferenceName(edtUReferenceName.getText().toString().trim());

                donation.setStatusPayment("Menunggu Verifikasi");

                donation.setPhoto(txtPhoto.getText().toString().trim());
                donation.setPhotoURL(txtPhotoURL.getText().toString().trim());

                donation.setCreatedBy(prefManager.getUserID());
                donation.setCreatedDate(mydate);

                if (!isInsert) {
                    donation.setModifiedBy(prefManager.getUserID());
                    donation.setModifiedDate(mydate);
                }

                donation.setIsActive(true);

                UserAccount user = new UserAccount();
                user.setID(prefManager.getUserID());
                user.setUserName(prefManager.getUserName());
                user.setName(prefManager.getName());
                user.setEmail(prefManager.getEmail());
                user.setMobilePhone(prefManager.getMobilePhone());
                user.setSex(prefManager.getSex());
                user.setLevel(Integer.parseInt(prefManager.getLevel()));
                user.setCreatedBy(prefManager.getUserID());
                user.setCreatedDate(mydate);
                user.setModifiedBy(prefManager.getUserID());
                user.setModifiedDate(mydate);
                user.setIsActive(true);

                Donor donor = new Donor();
                donor.setID("");
                donor.setUserName("");
                donor.setName(edtUName.getText().toString().trim());
                donor.setEmail(edtUEmail.getText().toString().trim());

                String mobilePhone = edtUMobilePhone.getText().toString().trim();
                if (mobilePhone != null && !mobilePhone.equals("") && mobilePhone.length() > 0 && mobilePhone.substring(0, 1).equals("0")) {
                    mobilePhone = "62" + mobilePhone.substring(1, mobilePhone.length());
                }

                donor.setMobilePhone(mobilePhone);
                donor.setSex("");
                donor.setLevel(5);
                donor.setCreatedBy(prefManager.getUserID());
                donor.setCreatedDate(mydate);
                donor.setModifiedBy(prefManager.getUserID());
                donor.setModifiedDate(mydate);
                donor.setIsActive(true);

                donation.setProgram(myProgram);
                donation.setUser(user);
                donation.setDonor(donor);

                donation.setReligion(myReligion);
                donation.setJob(myJob);
                donation.setAge(myAge);
                donation.setDomicile(myDomicile);
                donation.setCategory(myCategory);

                txtPhoto.setText(fileName + fileExtension);
                dbDonation
                        .child(myID)
                        .setValue(donation);

                if (isInsert) {
                    Query databaseDonationYearly = FirebaseDatabase.getInstance().getReference("donationyearly").orderByChild("tahunTransaksi").equalTo(year).limitToLast(360000);
                    databaseDonationYearly.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //clearing the previous artist list
                            int found = 0;
                            long total = dataSnapshot.getChildrenCount();

                            if (total > 0) {
                                //iterating through all the nodes
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    //getting artist
                                    Report artist = postSnapshot.getValue(Report.class);

                                    if (year == artist.getTahunTransaksi() && month == artist.getBulanTransaksi()) {
                                        artist.setJumlahTransaksi(artist.getJumlahTransaksi() + donation.getNominal());
                                        artist.setModifiedBy(prefManager.getUserID());
                                        artist.setModifiedDate(mydate);

                                        DatabaseReference dbDonationYearly = FirebaseDatabase.getInstance()
                                                .getReference("donationyearly");

                                        dbDonationYearly
                                                .child(artist.getID())
                                                .setValue(artist);

                                        found = 1;
                                    }
                                }
                            }

                            if (total == 0 || found == 0) {
                                DatabaseReference dbDonationYearly = FirebaseDatabase.getInstance()
                                        .getReference("donationyearly");

                                String myID = dbDonationYearly.push().getKey();

                                Report artist = new Report();
                                artist.setID(myID);
                                artist.setTahunTransaksi(year);
                                artist.setBulanTransaksi(month);
                                artist.setJumlahTransaksi(donation.getNominal());
                                artist.setCreatedBy(prefManager.getUserID());
                                artist.setCreatedDate(mydate);
                                artist.setModifiedBy(prefManager.getUserID());
                                artist.setModifiedDate(mydate);
                                artist.setIsActive(true);

                                dbDonationYearly
                                        .child(myID)
                                        .setValue(artist);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    Query databaseDonationYearlyPersonal = FirebaseDatabase.getInstance().getReference("donationyearlypersonal").orderByChild("userID").equalTo(prefManager.getUserID()).limitToLast(360000);
                    databaseDonationYearlyPersonal.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //clearing the previous artist list
                            int found = 0;
                            long total = dataSnapshot.getChildrenCount();

                            if (total > 0) {
                                //iterating through all the nodes
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    //getting artist
                                    ReportPersonal artist = postSnapshot.getValue(ReportPersonal.class);

                                    if (year == artist.getTahunTransaksi() && month == artist.getBulanTransaksi()) {
                                        artist.setJumlahTransaksi(artist.getJumlahTransaksi() + donation.getNominal());
                                        artist.setModifiedBy(prefManager.getUserID());
                                        artist.setModifiedDate(mydate);

                                        DatabaseReference dbDonationYearlyPersonal = FirebaseDatabase.getInstance()
                                                .getReference("donationyearlypersonal");

                                        dbDonationYearlyPersonal
                                                .child(artist.getID())
                                                .setValue(artist);

                                        found = 1;
                                    }
                                }
                            }

                            if (total == 0 || found == 0) {
                                DatabaseReference dbDonationYearlyPersonal = FirebaseDatabase.getInstance()
                                        .getReference("donationyearlypersonal");

                                String myID = dbDonationYearlyPersonal.push().getKey();

                                ReportPersonal artist = new ReportPersonal();
                                artist.setID(myID);
                                artist.setUserID(prefManager.getUserID());
                                artist.setTahunTransaksi(year);
                                artist.setBulanTransaksi(month);
                                artist.setJumlahTransaksi(donation.getNominal());
                                artist.setCreatedBy(prefManager.getUserID());
                                artist.setCreatedDate(mydate);
                                artist.setModifiedBy(prefManager.getUserID());
                                artist.setModifiedDate(mydate);
                                artist.setIsActive(true);

                                dbDonationYearlyPersonal
                                        .child(myID)
                                        .setValue(artist);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    //Do Something?
                }

                Toast.makeText(mycontext, "Donasi Anda telah berhasil disimpan. Kami akan segera mengkonfirmasinya.", Toast.LENGTH_SHORT).show();
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
