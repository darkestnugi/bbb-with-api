package com.example.bbbwithapi.presentation.account;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bbbwithapi.R;
import com.example.bbbwithapi.model.ReportPersonal;
import com.example.bbbwithapi.preference.PrefManager;
import com.example.bbbwithapi.presentation.aboutbbb.AboutBBBActivity;
import com.example.bbbwithapi.presentation.absence.AbsenceActivity;
import com.example.bbbwithapi.presentation.accountdetail.AccountDetailActivity;
import com.example.bbbwithapi.presentation.login.LoginActivity;
import com.example.bbbwithapi.presentation.passwordupdate.UpdatePasswordActivity;
import com.example.bbbwithapi.presentation.watch.WatchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment{
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    public static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View viewContainer;
    private Button btnLogout;
    private TextView tvChangePassword, tvChangePasswordExplanation, tvbbbBackground, tvbbbWatch, tvbbbAbsence,
            tvAvatar, tvAvatarName, tvAvatarPhone, tvAvatarEmail,
            tvCardUsernameDetail, tvCardTotalDonationMonthlyDetail, tvCardTotalDonationDetail;
    private ImageView ivPassword, ivbbbBackground, ivbbbWatch, ivbbbAbsence, ivAvatar, ivEditProfile;

    private Context mycontext;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics crashlytics;

    double totalDonateMonth = (double) 0;
    double totalDonateAll = (double) 0;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewContainer = inflater.inflate(R.layout.fragment_account, container, false);

        askPermission();
        defineIds();
        initUi();
        initAction();

        return viewContainer;
    }

    private void defineIds() {
        btnLogout = (Button) viewContainer.findViewById(R.id.btnLogout);
        tvChangePassword = (TextView) viewContainer.findViewById(R.id.tvChangePassword);
        tvChangePasswordExplanation = (TextView) viewContainer.findViewById(R.id.tvChangePasswordExplanation);
        tvbbbBackground = (TextView) viewContainer.findViewById(R.id.tvbbbBackground);
        tvbbbAbsence = (TextView) viewContainer.findViewById(R.id.tvbbbAbsence);
        tvbbbWatch = (TextView) viewContainer.findViewById(R.id.tvbbbWatch);
        tvAvatar = (TextView) viewContainer.findViewById(R.id.tvAvatar);
        tvAvatarName = (TextView) viewContainer.findViewById(R.id.tvAvatarName);
        tvAvatarEmail = (TextView) viewContainer.findViewById(R.id.tvAvatarEmail);
        tvAvatarPhone = (TextView) viewContainer.findViewById(R.id.tvAvatarPhoneNumber);
        tvCardUsernameDetail = (TextView) viewContainer.findViewById(R.id.tvCardUsernameDetail);
        tvCardTotalDonationMonthlyDetail = (TextView) viewContainer.findViewById(R.id.tvCardTotalDonationMonthlyDetail);
        tvCardTotalDonationDetail = (TextView) viewContainer.findViewById(R.id.tvCardTotalDonationDetail);
        ivbbbBackground = (ImageView) viewContainer.findViewById(R.id.ivbbbBackground);
        ivbbbAbsence = (ImageView) viewContainer.findViewById(R.id.ivbbbAbsence);
        ivbbbWatch = (ImageView) viewContainer.findViewById(R.id.ivbbbWatch);
        ivPassword = (ImageView) viewContainer.findViewById(R.id.ivIconLock);
        ivAvatar = (ImageView) viewContainer.findViewById(R.id.ivAvatar);
        ivEditProfile = (ImageView) viewContainer.findViewById(R.id.ivEditProfile);

        mycontext = requireActivity();
        prefManager = new PrefManager(requireContext());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            prefManager.removeAllPreference();
            mAuth.signOut();

            Toast.makeText(mycontext, "Waktu login Anda habis. Silakan Login kembali (1)", Toast.LENGTH_LONG).show();
            startActivity(new Intent(mycontext, LoginActivity.class));
            requireActivity().finish();
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
                                requireActivity().finish();
                            }
                        }
                    });
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        mFirebaseAnalytics.setUserProperty("userID", prefManager.getUserID());
        mFirebaseAnalytics.setUserProperty("userEmail", prefManager.getEmail());

        crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("userID", prefManager.getUserID());
        crashlytics.setCustomKey("userEmail", prefManager.getEmail());
    }

    private void initUi() {
        totalDonation();

        tvAvatarPhone.setText(prefManager.getMobilePhone());
        tvAvatarEmail.setText(prefManager.getEmail());
        tvAvatarName.setText(prefManager.getName());
        tvCardUsernameDetail.setText(prefManager.getUserName());

        ArrayList<String> tempInitials = new ArrayList<>();
        if (prefManager.getName() != null && !prefManager.getName().equals("") && prefManager.getName().indexOf(" ") >= 0) {
            String[] tempName = prefManager.getName().split(" ");

            for (String initials : tempName) {
                tempInitials.add(String.valueOf(initials.charAt(0)));
            }
        }

        if (mAuth.getCurrentUser().getPhotoUrl() != null) {
            tvAvatar.setVisibility(View.GONE);
            ivAvatar.setVisibility(View.VISIBLE);
            Glide.with(requireContext()).load(mAuth.getCurrentUser().getPhotoUrl()).into(ivAvatar);
        } else {
            StringBuilder temp = new StringBuilder();

            for (String initials : tempInitials) {
                temp.append(initials);
                if (temp.length() > 3) break;
            }

            tvAvatar.setVisibility(View.VISIBLE);
            ivAvatar.setVisibility(View.GONE);
            tvAvatar.setText(temp);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initAction() {
        View.OnClickListener changePasswordClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), UpdatePasswordActivity.class);
                startActivity(intent);
            }
        };

        View.OnClickListener bbbBackgroundClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), AboutBBBActivity.class);
                startActivity(intent);
            }
        };

        View.OnClickListener bbbWatchClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), WatchActivity.class);
                startActivity(intent);
            }
        };

        View.OnClickListener bbbAbsenceClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), AbsenceActivity.class);
                startActivity(intent);
            }
        };

        tvChangePasswordExplanation.setOnClickListener(changePasswordClickListener);
        tvChangePassword.setOnClickListener(changePasswordClickListener);
        ivPassword.setOnClickListener(changePasswordClickListener);

        tvbbbBackground.setOnClickListener(bbbBackgroundClickListener);
        ivbbbBackground.setOnClickListener(bbbBackgroundClickListener);

        tvbbbWatch.setOnClickListener(bbbWatchClickListener);
        ivbbbWatch.setOnClickListener(bbbWatchClickListener);

        tvbbbAbsence.setOnClickListener(bbbAbsenceClickListener);
        ivbbbAbsence.setOnClickListener(bbbAbsenceClickListener);

        btnLogout.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.format("%s", btnLogout.getId()));
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.menu_login));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.type_menu));
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            LayoutInflater factory = LayoutInflater.from(requireContext());
            View logoutView = factory.inflate(R.layout.layout_logout_popup_dialog, null);
            Dialog builder = new Dialog(requireActivity());
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);

            logoutView.findViewById(R.id.btnNegativeLogout).setOnClickListener(view1 -> {
                builder.dismiss();
            });

            logoutView.findViewById(R.id.btnPositiveLogout).setOnClickListener(view1 -> {
                prefManager.removeAllPreference();
                mAuth.signOut();

                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                builder.dismiss();
                startActivity(intent);
                requireActivity().finish();
            });

            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setContentView(logoutView);
            builder.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            builder.show();

        });

        ivEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), AccountDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initUi();
    }

    private void totalDonation() {
        final ProgressDialog progressDialog = new ProgressDialog(mycontext);
        progressDialog.setTitle("Downloading");
        progressDialog.show();
        progressDialog.setMessage("Downloading " + 1 + "%...");

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        Query databaseDonation = FirebaseDatabase.getInstance().getReference("donationyearlypersonal").orderByChild("userID").equalTo(prefManager.getUserID()).limitToLast(64);
        databaseDonation.addValueEventListener(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = 0;
                long totalChildren = snapshot.getChildrenCount();
                totalDonateMonth = (double) 0;
                totalDonateAll = (double) 0;

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        ReportPersonal artist = postSnapshot.getValue(ReportPersonal.class);

                        if (artist.getIsActive()) {
                            int transYear = 0;
                            int transMonth = 0;
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                            try {
                                transYear = artist.getTahunTransaksi();
                                transMonth = artist.getBulanTransaksi() + 1;

                                if (transYear == year && transMonth == month) {
                                    totalDonateMonth = totalDonateMonth + artist.getJumlahTransaksi();
                                }

                                totalDonateAll = totalDonateAll + artist.getJumlahTransaksi();
                            } catch (Exception err) {
                                Toast.makeText(requireContext(), "Error Parse: " + err.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    catch (Exception err) {
                        Toast.makeText(mycontext, "Error:" + err.getMessage() + " " + postSnapshot.getKey(), Toast.LENGTH_LONG).show();
                    }

                    count++;
                    double progress = (100.0 * count) / totalChildren;
                    progressDialog.setMessage("Downloading " + ((int) progress) + "%...");
                }

                tvCardTotalDonationMonthlyDetail.setText(String.format("Rp %,.2f", totalDonateMonth));
                tvCardTotalDonationDetail.setText(String.format("Rp %,.2f", totalDonateAll));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                double progress = 100.0;
                progressDialog.setMessage("Downloading " + ((int) progress) + "%...");
                progressDialog.dismiss();

                Toast.makeText(mycontext, "Internet tidak stabil. Mohon periksa kembali jaringan internet Anda", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void askPermission() {
        if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    WRITE_EXTERNAL_STORAGE_CODE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    READ_EXTERNAL_STORAGE_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.INTERNET)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    INTERNET,
                    Manifest.permission.INTERNET);
        }

        if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    ACCESS_FINE_LOCATION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    ACCESS_COARSE_LOCATION_CODE,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }
}
