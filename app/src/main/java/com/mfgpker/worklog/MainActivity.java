package com.mfgpker.worklog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mfgpker.worklog.data.Logs;
import com.mfgpker.worklog.data.RestClass;
import com.mfgpker.worklog.data.UserConfig;
import com.mfgpker.worklog.data.WorkType;
import com.mfgpker.worklog.databinding.ActivityMainBinding;
import com.mfgpker.worklog.firebase.FirebaseUtils;
import com.mfgpker.worklog.firebase.MyCallback;
import com.mfgpker.worklog.firebase.TimeUtils;
import com.mfgpker.worklog.log.MultipleLogActivity;
import com.mfgpker.worklog.log.TodayLogActivity;
import com.mfgpker.worklog.login.LoginActivity;
import com.mfgpker.worklog.stats.MonthStatsActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public  static UserConfig config = new UserConfig();

    private final FirebaseUtils firebase = new FirebaseUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(firebase.validUser()) {
            logout();
        }

        makeToolbar();
        Toast.makeText(MainActivity.this, "getCurrentUser: " + firebase.getUser().getUid() + ", " + firebase.getUser().getEmail(), Toast.LENGTH_SHORT).show();

        binding.mainSignout.setOnClickListener(view -> logout());

        binding.mainUserConfig.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, UserConfigActivity.class)));

        binding.mainTodayLog.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, TodayLogActivity.class)));

        binding.mainOverview.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, OverviewActivity.class)));

        binding.mainMultipleDays.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MultipleLogActivity.class)));

        binding.mainStats.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MonthStatsActivity.class)));

        firebase.getUserConfig((MyCallback<UserConfig>) value -> config = value);

        LocalDateTime getStartOfMonth = TimeUtils.getStartOfYear(LocalDateTime.now());
        LocalDateTime getWnsOfMonth = TimeUtils.getEndOfYear(LocalDateTime.now());

        Log.e("worklo", "getStartOfYear: " + getStartOfMonth.format(DateTimeFormatter.ISO_DATE), null);
        Log.e("worklo", "getEndOfYear: " + getWnsOfMonth.format(DateTimeFormatter.ISO_DATE), null);

        binding.button5.setOnClickListener(view -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("f7dkrh5pXYSxOhsBrAAA6a5MImr2").child("Days");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserConfig config_ = new UserConfig();
                    config_.currentCompany = "Oxyguard";

                    for(DataSnapshot day : snapshot.getChildren() ) {
                        RestClass test = day.getValue(RestClass.class);
                        Logs log = new Logs();

                        log.companyName = config_.currentCompany;

                        assert test != null;
                        if(test.date != null && !TextUtils.isEmpty(test.date)) {
                            log.date = LocalDateTime.parse(test.date + "T08:00:00");

                            //2022-02-02T16:48:40.731
                            if(test.start != null && !TextUtils.isEmpty(test.start)) {
                                String dd = test.date + "T"+test.start;
                                log.startTime = LocalDateTime.parse(dd);
                            } else {
                                log.startTime = null;
                            }

                            if(test.end != null && !TextUtils.isEmpty(test.end)) {
                                String dd = test.date + "T"+test.end;
                                log.endTime = LocalDateTime.parse(dd);
                            }

                            log.breakMinutes = 0;

                            if(TextUtils.equals(test.worktype, "NORMAL")) {
                                log.workType = WorkType.NORMAL;
                            } else if(TextUtils.equals(test.worktype, "FERIE")) {
                                log.workType = WorkType.HOLIDAY;
                            } else if(TextUtils.equals(test.worktype, "SYG")) {
                                log.workType = WorkType.SICK;
                            } else if(TextUtils.equals(test.worktype, "SPECIAL")) {
                                log.workType = WorkType.WORK_FROM_HOME;
                            }  else if(TextUtils.equals(test.worktype, "FRI")) {
                                log.workType = WorkType.FREE;
                            } else {
                                log.workType = WorkType.UNEMPLOYED;
                            }

                            Log.i("BBBBBB", "onDataChange: " + test.worktype);
                            firebase.setTodayLog(log);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }

    private void makeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }


    // Menu icons are inflated just as they were with actionbar
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    private  void logout() {
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

}