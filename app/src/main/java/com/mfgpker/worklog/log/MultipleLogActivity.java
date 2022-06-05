package com.mfgpker.worklog.log;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mfgpker.worklog.MainActivity;
import com.mfgpker.worklog.data.Logs;
import com.mfgpker.worklog.data.WorkType;
import com.mfgpker.worklog.databinding.ActivityMultipleLogBinding;
import com.mfgpker.worklog.firebase.FirebaseUtils;
import com.mfgpker.worklog.firebase.TimeUtils;
import com.mfgpker.worklog.selectModal.BetterActivityResult;
import com.mfgpker.worklog.selectModal.DateSelectModalType;
import com.mfgpker.worklog.selectModal.SelectDateActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MultipleLogActivity extends AppCompatActivity {
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    static final int START_TIME_REQUEST = 1;  // The request code
    static final int END_TIME_REQUEST = 2;  // The request code

    Context applicationContext;
    private final FirebaseUtils firebase = new FirebaseUtils();

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int numberOfDays = 0;
    private Logs log;

    private TextView txtNumberOfDays;
    private TextView txtTodayStart;
    private TextView txtTodayEnd;
    private Button btnSetLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMultipleLogBinding binding = ActivityMultipleLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        applicationContext = getApplicationContext();
        txtNumberOfDays = binding.multipleNumbersOfDays;
        txtTodayStart = binding.multipleStartDayMonth;
        txtTodayEnd = binding.multipleLastDayMonth;
        btnSetLog = binding.multipleSetLog;

        binding.multipleSetLog.setEnabled(false);

        binding.multipleStartDayMonth.setOnLongClickListener(view -> {

            openSelectDateModal(START_TIME_REQUEST, startDate);
            return false;
        });

        binding.multipleLastDayMonth.setOnLongClickListener(view -> {

            openSelectDateModal(END_TIME_REQUEST, startDate);
            return false;
        });

        binding.multipleSetLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editLogIntent = new Intent(getApplicationContext(), TodayLogActivity.class);

                Logs log_ = log == null ? new Logs() : log;
                log_.workType = WorkType.HOLIDAY;
                log_.companyName = MainActivity.config.currentCompany;
                log_.date = startDate;


                editLogIntent.putExtra(TodayLogActivity.ARG_LOG, log_);
                editLogIntent.putExtra(TodayLogActivity.ARG_DO_NOT_SAVE, true);

                activityLauncher.launch(editLogIntent, result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes

                        Intent data = result.getData();
                        assert data != null;
                        Bundle b = data.getExtras();

                        log = (Logs) b.get(TodayLogActivity.ARG_LOG);

                        binding.multipleSave.setEnabled(true);
                    }
                });

            }
        });

        binding.multipleSave.setEnabled(false);
        binding.multipleSave.setOnClickListener(view -> {
            log.date = startDate;
            log.companyName = MainActivity.config.currentCompany;
            setNumberofDays();

            for (int i = 0; i <= numberOfDays; i++) {
                firebase.setTodayLog(log);

                log.date = log.date.plusDays(1);
                if(log.startTime != null) {
                    log.startTime = log.startTime.plusDays(1);
                }
                if(log.endTime != null) {
                    log.endTime= log.endTime.plusDays(1);
                }
            }

            finish();
        });
    }

    private void openSelectDateModal(int request, LocalDateTime date) {
        Intent pickTimeIntent = new Intent(applicationContext, SelectDateActivity.class);

        pickTimeIntent.putExtra(SelectDateActivity.ARG_TYPE, DateSelectModalType.DATE_MODAL);
        pickTimeIntent.putExtra(SelectDateActivity.ARG_DATE, date);
        pickTimeIntent.putExtra(SelectDateActivity.ARG_REQUEST_CODE, request);

        activityLauncher.launch(pickTimeIntent, result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                assert result.getData() != null;

                Intent data = result.getData();
                Bundle b = data.getExtras();

                int requestCode = b.getInt(SelectDateActivity.ARG_REQUEST_CODE);
                LocalDateTime newMonth = (LocalDateTime) b.get(SelectDateActivity.ARG_DATE);

                if(requestCode == START_TIME_REQUEST) {
                    startDate = newMonth;
                } else {
                    endDate = newMonth;
                }

                setNumberofDays();
                setFirstDayView();
                setEndTimeView();

                if(startDate != null & endDate != null) {
                    btnSetLog.setEnabled(true);
                }
            }
        });
    }

    private void setNumberofDays() {
        if(startDate != null & endDate != null) {
            TimeUtils.TimeDifference timeDifference = TimeUtils.getTimeDifference(startDate, endDate);
            numberOfDays = (int) timeDifference.days;
        }

        txtNumberOfDays.setText("Number of days: " + numberOfDays);
    }

    private void setFirstDayView() {
        txtTodayStart.setText(startDate != null ? startDate.format(DateTimeFormatter.ISO_DATE) : "");
    }

    private void setEndTimeView() {
        txtTodayEnd.setText(endDate != null ? endDate.format(DateTimeFormatter.ISO_DATE) : "");
    }
}