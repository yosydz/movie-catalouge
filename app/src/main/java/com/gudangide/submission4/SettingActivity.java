package com.gudangide.submission4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.gudangide.submission4.notifications.NotificationReminder;

import static com.gudangide.submission4.notifications.NotificationReminder.ID_RELEASE;
import static com.gudangide.submission4.notifications.NotificationReminder.ID_REMINDER;
import static com.gudangide.submission4.notifications.NotificationReminder.TYPE_RELEASE;
import static com.gudangide.submission4.notifications.NotificationReminder.TYPE_REMINDER;

public class SettingActivity extends AppCompatActivity {
    private Switch releaseSwitch;
    private Switch reminderSwitch;

    private final String STATUS_REMINDER = "status_reminder";
    private final String STATUS_RELEASE = "status_release";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private NotificationReminder notificationReminder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        reminderSwitch = findViewById(R.id.switch_daily_reminder);
        releaseSwitch = findViewById(R.id.switch_release_reminder);

        notificationReminder = new NotificationReminder();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Setting");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        toolbar.setNavigationOnClickListener(view -> finish());

        sharedPreferences = getSharedPreferences("switch", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(STATUS_RELEASE, false)){
            releaseSwitch.setChecked(true);
        }
        if (sharedPreferences.getBoolean(STATUS_REMINDER, false)){
            reminderSwitch.setChecked(true);
        }

        releaseSwitch.setOnCheckedChangeListener((compoundButton, status) -> {
            if (status){
                notificationReminder.setRepeatingNotification(SettingActivity.this, TYPE_RELEASE, ID_RELEASE);
                editor.putBoolean(STATUS_RELEASE, true);
                editor.apply();
                Log.d("Status switch Release", String.valueOf(status));
            }else {
                notificationReminder.cancelNotification(SettingActivity.this, TYPE_RELEASE);
                editor.putBoolean(STATUS_RELEASE, false);
                editor.apply();
                Log.d("Status switch Release", String.valueOf(status));
            }
        });

        reminderSwitch.setOnCheckedChangeListener((compoundButton, status) -> {
            if (status){
                notificationReminder.setRepeatingNotification(SettingActivity.this, TYPE_REMINDER, ID_REMINDER);
                editor.putBoolean(STATUS_REMINDER, true);
                editor.apply();
                Log.d("Status switch Remainder", String.valueOf(status));
            }else {
                notificationReminder.cancelNotification(SettingActivity.this, TYPE_REMINDER);
                editor.putBoolean(STATUS_REMINDER, false);
                editor.apply();
                Log.d("Status switch Remainder", String.valueOf(status));
            }
        });
    }
}
