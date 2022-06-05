package com.mfgpker.worklog.log;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mfgpker.worklog.MainActivity;
import com.mfgpker.worklog.R;
import com.mfgpker.worklog.data.Logs;
import com.mfgpker.worklog.data.WorkType;
import com.mfgpker.worklog.firebase.FirebaseUtils;
import com.mfgpker.worklog.firebase.MyCallback;
import com.mfgpker.worklog.databinding.ActivityTodayLogBinding;
import com.mfgpker.worklog.selectModal.BetterActivityResult;
import com.mfgpker.worklog.selectModal.DateSelectModalType;
import com.mfgpker.worklog.selectModal.SelectDateActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TodayLogActivity extends AppCompatActivity {
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    public static final String ARG_DATE = "date";
    public static final String ARG_LOG = "log";
    public static final String ARG_DO_NOT_SAVE = "not-save";

    static final int START_TIME_REQUEST = 1;  // The request code
    static final int END_TIME_REQUEST = 2;  // The request code

    private ProgressBar progressBar;

    private final FirebaseUtils firebase = new FirebaseUtils();

    private LocalDateTime todayDate = LocalDateTime.now();
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private WorkType workType = WorkType.NORMAL;
    private boolean doNotSaveLog = false;

    private Button todayStartTime;
    private Button todayEndTime;
    private TextView txtTodayDate;
    private TextView txtTodayStart;
    private TextView txtTodayEnd;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTodayLogBinding binding = ActivityTodayLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeToolbar();

        progressBar = binding.indeterminateBar;
        progressBar.setVisibility(View.VISIBLE);

        todayStartTime = binding.todayStartTime;
        todayEndTime = binding.todayEndTime;
        txtTodayDate = binding.todayDate;
        txtTodayEnd = binding.todayEnd;
        txtTodayStart = binding.todayStart;

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.containsKey(ARG_DATE)) {
            todayDate = (LocalDateTime) b.get(ARG_DATE);
        }

        if(b != null && b.containsKey(ARG_DO_NOT_SAVE)) {
            doNotSaveLog = b.getBoolean(ARG_DO_NOT_SAVE);
        } else {
            doNotSaveLog = false;
        }

        if(b != null && b.containsKey(ARG_LOG)) {
            Logs log =  (Logs)b.get(ARG_LOG);
            todayDate = log.date;
            startDate = log.startTime;
            endDate = log.endTime;
            workType = log.workType;
            checkControl(workType);
            setDateView();
            setStartTimeView();
            setEndTimeView();
            progressBar.setVisibility(View.GONE);
        } else {
            setDateView();
            workType = WorkType.NORMAL;

            if(MainActivity.config != null) {
                firebase.getTodayLog(todayDate, MainActivity.config, (MyCallback<Logs>) log -> {
                    progressBar.setVisibility(View.GONE);
                    if (log != null) {
                        todayDate = log.date;
                        startDate = log.startTime;
                        endDate = log.endTime;
                        binding.todayBreakMinutes.setText(String.valueOf(log.breakMinutes));
                        binding.todayWorktyper.setSelection(log.workType.toInt());

                        checkControl(workType);
                    }
                });
            }
        }

        binding.todayWorktyper.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, WorkType.values()));
        binding.todayWorktyper.setSelection(workType.toInt());

        binding.todayWorktyper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                workType = (WorkType)(binding.todayWorktyper.getSelectedItem());

                checkControl(workType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.todayStartTime.setOnClickListener(view -> {
            startDate = LocalDateTime.now();
            binding.todayStart.setText(startDate.format(formatter));
            setStartTimeView();
        });


        binding.todayEndTime.setOnClickListener(view -> {
            endDate = LocalDateTime.now();
           setEndTimeView();
        });

        binding.todayStart.setOnLongClickListener(view -> {

            openSelectTimeModal(START_TIME_REQUEST, getApplicationContext(), startDate);
            return false;
        });

        binding.todayEnd.setOnLongClickListener(view -> {

            openSelectTimeModal(END_TIME_REQUEST, getApplicationContext(), endDate);
            return false;
        });

        binding.todayDate.setOnLongClickListener(view -> {

            openSelectDateModal(getApplicationContext(), todayDate);
            return false;
        });

        binding.todaySave.setOnClickListener(view -> {
            if(allowToSave()) {

                Logs log = new Logs();
                log.companyName = MainActivity.config.currentCompany;
                log.date = todayDate;
                log.startTime = startDate;
                log.endTime = endDate;
                String bm = binding.todayBreakMinutes.getText().toString();
                log.breakMinutes = !TextUtils.isEmpty(bm) && TextUtils.isDigitsOnly(bm) ?
                        Integer.parseInt(binding.todayBreakMinutes.getText().toString()) : 0;
                log.workType = (WorkType)(binding.todayWorktyper.getSelectedItem());

                if(!doNotSaveLog) {
                    firebase.setTodayLog(log);
                }
                Toast.makeText(TodayLogActivity.this, "saved?", Toast.LENGTH_SHORT).show();

                Intent returnIntent = new Intent();
                returnIntent.putExtra(ARG_DATE, log);
                returnIntent.putExtra(ARG_LOG, log);
                setResult(Activity.RESULT_OK, returnIntent);

                finish();
            }
        });
    }

    public  boolean allowToSave() {
        if(workType == WorkType.NORMAL || workType == WorkType.WORK_FROM_HOME) {
            return startDate != null &&
                    endDate != null &&
                    todayDate != null;
        } else {
            return todayDate != null;
        }
    }

    public void checkControl(WorkType workType) {
        if(workType == WorkType.NORMAL || workType == WorkType.WORK_FROM_HOME) {
            todayStartTime.setEnabled(true);
            todayEndTime.setEnabled(true);
        } else {
            startDate = null;
            endDate = null;
            todayStartTime.setEnabled(false);
            todayEndTime.setEnabled(false);
        }
        setDateView();
        setStartTimeView();
        setEndTimeView();
    }

    private void setDateView() {
        txtTodayDate.setText(todayDate != null ? todayDate.format(DateTimeFormatter.ISO_DATE) : "");
    }

    private void setStartTimeView() {
        txtTodayStart.setText(startDate != null ? startDate.format(formatter) : "");
    }

    private void setEndTimeView() {
        txtTodayEnd.setText(endDate != null ? endDate.format(formatter) : "");
    }

    private void openSelectTimeModal(int requestCode, Context cont, LocalDateTime date) {
        Intent pickTimeIntent = new Intent(cont, SelectDateActivity.class);

        pickTimeIntent.putExtra(SelectDateActivity.ARG_TYPE, DateSelectModalType.TIME_MODAL);
        pickTimeIntent.putExtra(SelectDateActivity.ARG_DATE, date);
        pickTimeIntent.putExtra(SelectDateActivity.ARG_REQUEST_CODE, requestCode);

        activityLauncher.launch(pickTimeIntent, result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                assert result.getData() != null;
                handleActivityLauncher(result.getData());
            }
        });
    }

    private void openSelectDateModal(Context cont, LocalDateTime date) {
        Intent pickTimeIntent = new Intent(cont, SelectDateActivity.class);

        pickTimeIntent.putExtra(SelectDateActivity.ARG_TYPE, DateSelectModalType.DATE_MODAL);
        pickTimeIntent.putExtra(SelectDateActivity.ARG_DATE, date);
        pickTimeIntent.putExtra(SelectDateActivity.ARG_REQUEST_CODE, 0);

        activityLauncher.launch(pickTimeIntent, result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                assert result.getData() != null;
                handleActivityLauncher(result.getData());
            }
        });
    }

    private void handleActivityLauncher( Intent data) {
        Bundle b = data.getExtras();

        LocalDateTime date = (LocalDateTime) b.get(SelectDateActivity.ARG_DATE);
        int requestCode = b.getInt(SelectDateActivity.ARG_REQUEST_CODE);
        DateSelectModalType modalType = (DateSelectModalType)b.get(SelectDateActivity.ARG_TYPE);

        Log.i("MFGPKERer", "date: " + date.format(DateTimeFormatter.ISO_DATE_TIME));
        Log.i("MFGPKERer", "requestCode: " + requestCode);
        Log.i("MFGPKERer", "modalType: " + modalType);

        switch (modalType) {
            case DATE_MODAL:
                todayDate = date;
                break;
            case TIME_MODAL:
                if(requestCode == START_TIME_REQUEST) {
                    startDate = date;
                } else if(requestCode == END_TIME_REQUEST) {
                    endDate = date;
                }
                break;
        }

        setDateView();
        setStartTimeView();
        setEndTimeView();
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
}