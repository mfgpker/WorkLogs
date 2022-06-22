package com.mfgpker.worklog.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mfgpker.worklog.data.Logs;
import com.mfgpker.worklog.data.UserConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class FirebaseUtils {

    private final FirebaseAuth auth;
    private final FirebaseUser user;

    public FirebaseUtils() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    public  FirebaseUser getUser() {
        return user;
    }

    public  FirebaseAuth getAuth() {
        return auth;
    }

    public  boolean validUser() {
        return user == null;
    }

    public DatabaseReference getCurrentUserReference() {
        if(user == null) {
            return null;
        }

        return  FirebaseDatabase.getInstance().getReference().child("work-logs").child(user.getUid());
    }

    public DatabaseReference getUserReference(String uid) {
        return  FirebaseDatabase.getInstance().getReference().child("work-logs").child(uid);
    }

    public void getUserConfig(MyCallback<UserConfig> myCallback){
        if(user == null) {
            return;
        }

        DatabaseReference ref = this.getCurrentUserReference().child("config");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserConfig config = snapshot.getValue(UserConfig.class);

                if(config == null) {
                    config = new UserConfig();
                }

                myCallback.onCallback(config);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setCurrentUserConfig(UserConfig config) {

        DatabaseReference ref = this.getCurrentUserReference().child("config");

        ref.setValue(config);
    }

    public void setTodayLog(Logs log) {
        DatabaseReference logsRef = this.getCurrentUserReference().child("Logs");

        String year = String.valueOf(log.date.getYear());
        String month = log.date.getMonth().toString();
        String day = String.valueOf(log.date.getDayOfMonth());

        LogsWrapper wrapper = new LogsWrapper(log);

        logsRef.child(log.companyName)
                .child("formatted")
                .child(year)
                .child(month)
                .child(day)
                .setValue(wrapper);

        Long timestamp = TimeUtils.getStartOfDayEpoch(log.date);

        logsRef.child(log.companyName)
                .child("all")
                .child(String.valueOf(timestamp)).
                setValue(wrapper);
    }

    public void getTodayLog(LocalDateTime todayDate, UserConfig config, MyCallback<Logs> myCallback) {
        DatabaseReference logsRef = this.getCurrentUserReference().child("Logs");

        Long start = TimeUtils.getStartOfDayEpoch(todayDate);

        DatabaseReference dayRef = logsRef.child(config.currentCompany).child("all");

        Query sortQuery = dayRef.orderByKey().equalTo(String.valueOf(start));
        sortQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LogsWrapper wrapper = null;

                for (DataSnapshot s : snapshot.getChildren()) {
                    wrapper = s.getValue(LogsWrapper.class);
                    break;
                }

                if(wrapper != null) {

                    myCallback.onCallback(new Logs(wrapper));

                } else {
                    myCallback.onCallback(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //logsRef.child(String.valueOf(year));
    }

    public void getAllLogs(UserConfig config, ArrayList<Logs> logs, MyCallback<ArrayList<Logs>> myCallback){
        if(user == null) {
            return;
        }
        DatabaseReference ref = this.getCurrentUserReference()
                .child("Logs")
                .child(config.currentCompany)
                .child("all");

        Query sortQuery = ref.orderByKey();

        sortQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logs.clear();

                for(DataSnapshot day : snapshot.getChildren()) {
                    LogsWrapper wrapper = day.getValue(LogsWrapper.class);
                    if (wrapper != null) {
                        logs.add(new Logs(wrapper));
                    }
                }
                Collections.sort(logs);
                myCallback.onCallback(logs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getLogsYear(UserConfig config, String year, ArrayList<Logs> logs, MyCallback<ArrayList<Logs>> myCallback){
        if(user == null) {
            return;
        }
        DatabaseReference ref = this.getCurrentUserReference()
                .child("Logs")
                .child(config.currentCompany)
                .child(year);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logs.clear();

                for(DataSnapshot month : snapshot.getChildren()) {
                    for(DataSnapshot day : month.getChildren()) {
                        LogsWrapper wrapper = day.getValue(LogsWrapper.class);

                        if (wrapper != null) {
                            logs.add(new Logs(wrapper));
                        }
                    }
                }
                Collections.sort(logs);
                myCallback.onCallback(logs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getLogsLast30Days(UserConfig config, ArrayList<Logs> logs, MyCallback<ArrayList<Logs>> myCallback){
        if(user == null) {
            return;
        }
        DatabaseReference ref = this.getCurrentUserReference()
                .child("Logs")
                .child(config.currentCompany)
                .child("all");

        Query sortQuery = ref.limitToLast(30);

        sortQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logs.clear();
                Log.e("worklo", "snapshot: " + snapshot , null);
                for(DataSnapshot day : snapshot.getChildren()) {
                    LogsWrapper wrapper = day.getValue(LogsWrapper.class);
                    if (wrapper != null) {
                        logs.add(new Logs(wrapper));

                    }
                }
                Collections.sort(logs);
                myCallback.onCallback(logs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getLogsBetween(UserConfig config, ArrayList<Logs> logs, LocalDateTime start, LocalDateTime end,  MyCallback<ArrayList<Logs>> myCallback){
        if(user == null) {
            return;
        }
        DatabaseReference ref = this.getCurrentUserReference()
                .child("Logs")
                .child(config.currentCompany)
                .child("all");

        long startStamp = TimeUtils.getTimeStamp(start);
        long endStamp = TimeUtils.getTimeStamp(end);

        Query sortQuery = ref.startAt(String.valueOf(startStamp)).endAt(String.valueOf(endStamp)).orderByKey();

        sortQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logs.clear();

                for(DataSnapshot day : snapshot.getChildren()) {
                    LogsWrapper wrapper = day.getValue(LogsWrapper.class);
                    if (wrapper != null) {
                        logs.add(new Logs(wrapper));

                    }
                }
                Collections.sort(logs);
                myCallback.onCallback(logs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}