package com.mfgpker.worklog;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mfgpker.worklog.adapter.OverviewAdapter;
import com.mfgpker.worklog.data.Logs;
import com.mfgpker.worklog.databinding.ActivityOverciewBinding;
import com.mfgpker.worklog.firebase.FirebaseUtils;
import com.mfgpker.worklog.firebase.MyCallback;
import com.mfgpker.worklog.log.TodayLogActivity;
import com.mfgpker.worklog.selectModal.BetterActivityResult;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OverviewActivity extends AppCompatActivity {
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    private final FirebaseUtils firebase = new FirebaseUtils();

    private ProgressBar progressBar;
    private OverviewAdapter overviewAdapter;

    private final ArrayList<Logs> logs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityOverciewBinding binding = ActivityOverciewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressBar = binding.indeterminateBar;
        ListView listView = binding.overviewListview;
        Context context = getApplicationContext();

        overviewAdapter = new OverviewAdapter(context, logs);
        listView.setAdapter(overviewAdapter);

        listView.setOnItemLongClickListener((adapterView, view, position, l) -> {
            Logs log = logs.get(position);

            Intent editLogIntent = new Intent(getApplicationContext(), TodayLogActivity.class);
            editLogIntent.putExtra(TodayLogActivity.ARG_LOG, log);

            activityLauncher.launch(editLogIntent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes

                    Intent data = result.getData();
                    assert data != null;
                    Bundle b = data.getExtras();

                    Logs editLog = (Logs) b.get(TodayLogActivity.ARG_LOG);

                    logs.set(position, editLog);
                    overviewAdapter.notifyDataSetChanged();
                }
            });

            return false;
        });

        initFirebase(logs);
    }

    private void ConfigReady() {
        progressBar.setVisibility(View.GONE);
        //
        overviewAdapter.notifyDataSetChanged();
    }

    public void getThisMonthLogs(ArrayList<Logs> logsDB) {
        firebase.getLogsLast30Days(
                MainActivity.config,
                logsDB,
                (MyCallback<ArrayList<Logs>>) logs -> ConfigReady());
    }

    public void getThisYearLogs(ArrayList<Logs> logsDB) {
        LocalDateTime now = LocalDateTime.now();

        firebase.getLogsYear(MainActivity.config,
                String.valueOf(now.getYear()),
                logsDB,
                (MyCallback<ArrayList<Logs>>) logs -> ConfigReady());
    }

    public void getAllLogs(ArrayList<Logs> logsDB) {
        firebase.getAllLogs(MainActivity.config, logsDB, (MyCallback<ArrayList<Logs>>) logs -> ConfigReady());
    }

    private void initFirebase(ArrayList<Logs> logsDB) {
        progressBar.setVisibility(View.VISIBLE);
        getThisMonthLogs(logsDB);
    }
}