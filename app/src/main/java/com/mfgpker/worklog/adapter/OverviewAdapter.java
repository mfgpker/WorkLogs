package com.mfgpker.worklog.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mfgpker.worklog.R;
import com.mfgpker.worklog.data.Logs;
import com.mfgpker.worklog.firebase.TimeUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class OverviewAdapter extends ArrayAdapter<Logs> {

    // View lookup cache
    private static class ViewHolder {
        TextView listDate;
        TextView listTime;
        TextView listHours;
        TextView listEndTime;
        TextView listWorkType;
    }

    private final Context context;
    private final ArrayList<Logs> logs;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public OverviewAdapter(@NonNull Context context, @NonNull ArrayList<Logs> logs) {
        super(context, R.layout.overview_list_item, logs);

        this.context = context;
        this.logs = logs;
    }

    private int lastPosition = -1;

    public View getView(int position, View convertView, ViewGroup parent) {
        Logs log = logs.get(position);
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.overview_list_item, parent, false);

            viewHolder.listDate = (TextView) convertView.findViewById(R.id.listDate);
            viewHolder.listTime = (TextView) convertView.findViewById(R.id.listTime);
            viewHolder.listHours = (TextView) convertView.findViewById(R.id.listHours);
            viewHolder.listEndTime = (TextView) convertView.findViewById(R.id.listEndTime);
            viewHolder.listWorkType = (TextView) convertView.findViewById(R.id.listWorkType);

            result= convertView;
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;


        viewHolder.listDate.setText(log.date.format(DateTimeFormatter.ISO_DATE) );

        if(log.startTime != null && log.endTime != null ) {
            String timeStr = log.startTime.format(formatter) + " -> " + log.endTime.format(formatter);
            viewHolder.listTime.setText(timeStr);

            TimeUtils.TimeDifference time = TimeUtils.getTimeDifference(log.startTime, log.endTime);

            Log.i("WWW", "minutes: " + time.minutes);
            Log.i("WWW", "hours: " + time.hours);
            viewHolder.listHours.setText(String.format("%sh %sm", time.hours, time.minutes));

        } else {
            viewHolder.listTime.setText("");
            viewHolder.listHours.setText("0h 0m");
        }

        viewHolder.listEndTime.setText("-");
        viewHolder.listWorkType.setText(log.workType != null ? log.workType.toString() : "-");

        return convertView;
    };

    @Override
    public int getCount() {
        return logs.size();
    }

    public  Logs getItem(int position) {

        return logs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void add(@Nullable Logs log) {
        super.add(log);

        logs.add(log);
    }

    @Override
    public void addAll(Logs... items) {
        super.addAll(items);
        for(Logs log : items) {
            logs.add(log);
        }
    }

    @Override
    public void insert(@Nullable Logs log, int index) {
        super.insert(log, index);
        logs.add(log);
    }

    @Override
    public void remove(@Nullable Logs object) {
        super.remove(object);

        logs.remove(object);
    }

    @Override
    public void clear() {
        super.clear();
        logs.clear();
    }
}
