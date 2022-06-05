package com.mfgpker.worklog.stats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mfgpker.worklog.MainActivity;
import com.mfgpker.worklog.data.Logs;
import com.mfgpker.worklog.databinding.ActivityMonthStatsBinding;
import com.mfgpker.worklog.firebase.FirebaseUtils;
import com.mfgpker.worklog.firebase.MyCallback;
import com.mfgpker.worklog.firebase.TimeUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MonthStatsActivity extends AppCompatActivity {

    private final FirebaseUtils firebase = new FirebaseUtils();
    private final ArrayList<Logs> logs = new ArrayList<>();
    private final ArrayList<LocalDateTime> workDays = new ArrayList<>();

    public Stats stat  = new Stats();
    public int year = LocalDateTime.now().getYear();
    public int month = LocalDateTime.now().getMonthValue();
    public LocalDateTime date = TimeUtils.getDate(year, month);

    private TextView workText;
    private TextView normalText;
    private TextView holidayText;
    private TextView sickText;
    private TextView homeText;
    private TextView awayText;
    private TextView freeText;
    private TextView unemployedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMonthStatsBinding binding = ActivityMonthStatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("workloLOG", "getLogs: ");
        getWorkDays();
        getLogs();

    }

    public void getWorkDays() {
        LocalDateTime workDate = TimeUtils.getStartOfMonth(date);
        int lengthOfMonth = workDate.toLocalDate().lengthOfMonth();
        int currentDay = workDate.getDayOfWeek().getValue();

        workDays.clear();
        while(lengthOfMonth > 0) {

            if(currentDay <= 5) {
                Log.d("getWorkDays", "getWorkDays: " + workDate.getDayOfWeek().getValue());
                workDays.add(workDate);
            }

            workDate = workDate.plusDays(1);
            currentDay = workDate.getDayOfWeek().getValue();
            lengthOfMonth--;
        }


        Log.d("getWorkDays", "END?: " + workDate.getDayOfWeek().getValue() + " : " + lengthOfMonth);


    }


    public  void getLogs() {
        LocalDateTime dateEnd = TimeUtils.getEndOfMonth(date);
        firebase.getLogsBetween(MainActivity.config, logs, date, dateEnd, value -> {
            Log.e("worklo", "logs: " + logs.size() , null);
            stat  = new Stats();

            for(int i = 0; i < logs.size(); i++) {
                Logs log = logs.get(i);

                switch (log.workType) {
                    case NORMAL:
                        stat.addNormal();
                        break;
                    case HOLIDAY:
                        stat.addHoliday();
                        break;
                    case SICK:
                        stat.addSick();
                        break;
                    case WORK_FROM_HOME:
                        stat.addWorkFromHome();
                        break;
                    case WORK_FROM_AWAY:
                        stat.addWorkFromAway();
                        break;
                    case FREE:
                        stat.addFree();
                        break;
                    case UNEMPLOYED:
                        stat.addUnemployed();
                        break;
                }

            }

        });
    }
}