package com.team3.fitnesstrackerapp3;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationChannels extends Application {
    public static final String CHANNEL_1_ID = "inactivity";
    public static final String CHANNEL_2_ID = "location";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel inactivity = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Inactivity",
                    NotificationManager.IMPORTANCE_HIGH
            );
            inactivity.setDescription("You've been inactive for more than one hour." +
                    "Please take at least two minutes to walk.");

            NotificationChannel location = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Location",
                    NotificationManager.IMPORTANCE_HIGH
            );
            location.setDescription("Please park farther from your destination as" +
                    "you haven't met your daily goal yet.");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(inactivity);
            notificationManager.createNotificationChannel(location);
        }
    }
}
