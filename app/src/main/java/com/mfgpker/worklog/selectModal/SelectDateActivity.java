package com.mfgpker.worklog.selectModal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.mfgpker.worklog.databinding.ActivitySelectDateBinding;
import com.mfgpker.worklog.databinding.ActivitySelectTimeBinding;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SelectDateActivity extends AppCompatActivity {

    public static final String ARG_TYPE = "type";
    public static final String ARG_REQUEST_CODE = "requestCode";
    public static final String ARG_DATE = "date";

    private TimePicker timePicker;
    private Button timePickerSave;

    private DatePicker datePicker;
    private Button datePickerSave;

    private LocalDateTime date = LocalDateTime.now();
    private int requestCode;
    private DateSelectModalType modalType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectDateBinding dateBinding = ActivitySelectDateBinding.inflate(getLayoutInflater());
        ActivitySelectTimeBinding timeBinding = ActivitySelectTimeBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        modalType = (DateSelectModalType)b.get(ARG_TYPE);
        requestCode =b.getInt(ARG_REQUEST_CODE);

        if(b.containsKey(ARG_DATE)) {
            date = (LocalDateTime) b.get(ARG_DATE);
        }

        if(date == null) {
            date = LocalDateTime.now();
        }

        if(modalType == null) {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
            return;
        }

        switch (modalType) {
            case DATE_MODAL:
                setContentView(dateBinding.getRoot());
                break;
            case TIME_MODAL:
                setContentView(timeBinding.getRoot());
                break;
        }

        timePicker = timeBinding.SelectDateTimePicker;
        timePickerSave = timeBinding.selectTimeSave;

        timePicker.setHour(date.getHour());
        timePicker.setMinute(date.getMinute());

        datePicker = dateBinding.SelectTimeDatoPicker;
        datePickerSave = dateBinding.SelectTimeSave;

        datePicker.init(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), null);

        datePickerSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        timePickerSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    public void save() {
        Intent returnIntent = new Intent();

        switch (modalType) {
            case DATE_MODAL:
                date = LocalDateTime.of(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), date.getHour(), date.getMinute());
                break;
            case TIME_MODAL:
                int h = timePicker.getHour();
                int m = timePicker.getMinute();
                date = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), h, m);
                break;
        }

        returnIntent.putExtra(SelectDateActivity.ARG_DATE, date);
        returnIntent.putExtra(SelectDateActivity.ARG_TYPE, modalType);
        returnIntent.putExtra(SelectDateActivity.ARG_REQUEST_CODE, requestCode);

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}