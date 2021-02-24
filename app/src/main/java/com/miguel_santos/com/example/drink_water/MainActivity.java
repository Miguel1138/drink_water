package com.miguel_santos.com.example.drink_water;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // TODO: 24/02/2021  Inserir um MainPresenter para delegar a atribuições de views

    private static final String KEY_NOTIFY = "key_notify";
    private static final String KEY_HOUR = "key_hour";
    private static final String KEY_MINUTES = "Key_minutes";
    private static final String KEY_INTERVAL = "key_interval";
    //

    private TimePicker clock;
    private EditText edtInterval;
    private Button btnNotify;

    private SharedPreferences storage;
    private boolean isActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clock = findViewById(R.id.timePicker_clock);
        clock.setIs24HourView(true);
        edtInterval = findViewById(R.id.edt_interval);
        btnNotify = findViewById(R.id.btn_create_alarm);

        storage = getSharedPreferences("storage", Context.MODE_PRIVATE);
        isActivated = storage.getBoolean("KEY_NOTIFY", false);

        if (isActivated) {
            setupUI(isActivated, storage);
        }

        btnNotify.setOnClickListener(notifyListener);
    }


    private View.OnClickListener notifyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isActivated) {
                if (!isIntervalValid()) {
                    return;
                }
                int interval = Integer.parseInt(edtInterval.getText().toString());
                int hour = clock.getHour();
                int minutes = clock.getMinute();

                updateStorage(true, hour, minutes, interval);
                setupUI(true, storage);
                setupNotification(true, hour, minutes, interval);

                isActivated = true;
            } else {
                updateStorage(false, 0, 0, 0);
                setupUI(false, storage);
                setupNotification(false, 0, 0, 0);

                isActivated = false;
            }
        }
    };

    private void updateStorage(boolean isAdded, int hour, int minutes, int interval) {
        SharedPreferences.Editor editor = storage.edit();
        editor.putBoolean(KEY_NOTIFY, isAdded);

        if (isAdded) {
            editor.putInt(KEY_HOUR, hour);
            editor.putInt(KEY_MINUTES, minutes);
            editor.putInt(KEY_INTERVAL, interval);
        } else {
            editor.remove(KEY_HOUR);
            editor.remove(KEY_MINUTES);
            editor.remove(KEY_INTERVAL);
        }
        editor.apply();
    }

    private void setupUI(boolean isActivated, SharedPreferences storage) {
        // Muda o botão e salva a hora e minuto de acordo o clique.
        if (isActivated) {
            btnNotify.setText(R.string.btn_msg_pause);
            btnNotify.setBackgroundResource(R.drawable.bg_btn_clicked_rounded);

            edtInterval.setText(String.valueOf(storage.getInt(KEY_INTERVAL, 0)));
            clock.setHour(storage.getInt(KEY_HOUR, clock.getHour()));
            clock.setMinute(storage.getInt(KEY_MINUTES, clock.getMinute()));
        } else {
            btnNotify.setText(R.string.create_alarm);
            btnNotify.setBackgroundResource(R.drawable.bg_btn_rounded);
        }
    }

    private void setupNotification(boolean added, int hour, int minutes, int interval) {
        Intent notificationIntent = new Intent(MainActivity.this, NotificationPublisher.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (added) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, 0);

            notificationIntent.putExtra(NotificationPublisher.getKeyNotificationId(), 1);
            notificationIntent.putExtra(NotificationPublisher.getKeyNotification(), "Hora de beber água");

            PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0,
                    notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    interval * 60 * 100, broadcast);
        } else {
            PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0,
                    notificationIntent, 0);
            alarmManager.cancel(broadcast);
        }
    }

    private boolean isIntervalValid() {
        String interval = edtInterval.getText().toString();
        if (interval.isEmpty()) {
            alert(R.string.error_interval_not_selected);
            return false;
        }
        if (interval.startsWith("0")) {
            int intervalInteger = Integer.parseInt(interval);
            if (intervalInteger <= 0) {
                alert(R.string.error_interval_value_zero);
                return false;
            }
        }
        return true;
    }

    private void alert(int resID) {
        Toast.makeText(getBaseContext(), resID, Toast.LENGTH_SHORT).show();
    }

}