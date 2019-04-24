package com.team3.fitnesstrackerapp3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class HomeFragment extends Fragment implements SensorEventListener {

    private static String MY_PREFS = "Values";

    private ProgressBar stepProgress;
    private SensorManager sensorManager;
    private Sensor sensor;
    private int progress = 0;
    private int notificationStop = 0;
    private int weight = 160;
    private TextView stepCount;
    private TextView calorieCount;
    private boolean running = true;
    private float reset = 0;
    private int today = 0;
    private int lastTimeStarted = 0;
    private ProgressBar calorieProgress;
    private TextView distanceCount;
    private ProgressBar distanceProgress;
    private int progressCheck = 0;
    private NotificationManagerCompat notificationManagerCompat;
    private Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private Context context;
    private volatile boolean stopThread = false;
    public static final String CHANNEL_1_ID = "inactivity";
    private int dailyGoal = 5000;
    private int height = 66;
    private float calories = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        stepProgress = view.findViewById(R.id.progressBar_Step_Count); //Gets progress bar from activity_main.xml
        stepCount = view.findViewById(R.id.textView_Step_Count);
        calorieCount = view.findViewById(R.id.textView_Calories);
        calorieProgress = view.findViewById(R.id.progressBar_Calories);
        distanceCount = view.findViewById(R.id.textView_Distance);
        distanceProgress = view.findViewById(R.id.progressBar_Disance);

        SharedPreferences settings = getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.getInt("dailyGoal") >= 1000) {
                dailyGoal = bundle.getInt("dailyGoal");
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("daily_goal", dailyGoal);
                editor.apply();
            }

            if (bundle.getInt("height") >= 1) {
                weight = bundle.getInt("weight");
                height = bundle.getInt("height");
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("userWeight", weight);
                editor.putInt("userHeight", height);
                editor.apply();
            }
        }

        dailyGoal = settings.getInt("daily_goal", 5000);
        weight = settings.getInt("userWeight", 160);
        height = settings.getInt("userHeight", 66);

        stepProgress.setMax(dailyGoal);
        stepProgress.setProgress(0);
        calorieProgress.setMax(dailyGoal / 40);
        calorieProgress.setProgress(0);
        distanceProgress.setMax(dailyGoal / 19);
        distanceProgress.setProgress(0);

        notificationManagerCompat = NotificationManagerCompat.from(context); //Create the NotificationManager

        progressCheck = progress;

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE); //Gets the sensor manager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER); //Gets step count sensor

        context = getContext().getApplicationContext();

        InactivityCheck inactivityCheck = new InactivityCheck();
        new Thread(inactivityCheck).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;

        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI); //Registers footsteps
        } else {
            Toast.makeText(getActivity(), "No sensor detected. App needs sensor. Sorry.", Toast.LENGTH_LONG).show(); //Will show if phone doesn't have built in sensor
        }


        SharedPreferences settings = getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DAY_OF_YEAR);

        while (today != lastTimeStarted) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("last_time_started", today);
            editor.apply();
            break;
        }

        reset = settings.getFloat("resetCheck", 0);
        notificationStop = settings.getInt("notifications", 0);
        progress = settings.getInt("progress", 0);

        while (today != lastTimeStarted) {
            notificationStop = 0;
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("notifications", notificationStop);
            editor.apply();
            break;
        }

        stopThread = false;
    }

    public void onPause() {
        super.onPause();
        running = false; //Stops running once app is paused in activity state
        stopThread = true;
    }

    public void onSensorChanged(SensorEvent event) {
        if (running) {
            progress = (int) (event.values[0] - reset);
            SharedPreferences settings = getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("progress", progress);
            editor.apply();
        }

        stepCount.setText(String.valueOf(progress)); //Shows the count of the steps everyday
        stepProgress.setProgress(progress);

        Float weightFloat = (float) weight;

        if(height < 66) {
            calories = Float.valueOf((weightFloat / 4400) * progress); //Gets the value of calories
            calorieProgress.setProgress((int) ((weightFloat / 4400) * (progress))); //Sets progress of meter to the calories
        }

        if (height >= 66 && height <= 71) {
            calories = Float.valueOf((weightFloat / 4000) * progress); //Gets the value of calories
            calorieProgress.setProgress((int) ((weightFloat / 4000) * (progress))); //Sets progress of meter to the calories
        }

        if(height > 71) {
            calories = Float.valueOf((weightFloat / 3600) * progress); //Gets the value of calories
            calorieProgress.setProgress((int) ((weightFloat / 3600) * (progress))); //Sets progress of meter to the calories
        }

        calorieCount.setText(String.format("%, .2f", calories)); //Sets text to the calories and formats to 2 decimal spots

        Float progressFloat = (float) progress;
        Float miles = Float.valueOf(progressFloat / 2200); //Gets the value of distance
        distanceCount.setText(String.format("%, .2f", miles));
        distanceProgress.setProgress((int) (miles * 100));

        while (today != lastTimeStarted) { // Resets the daily step count everyday
            reset = event.values[0];
            SharedPreferences settings = getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat("resetCheck", reset);
            editor.apply();
            break;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class InactivityCheck implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (stopThread)
                    return;
                try {
                    Thread.sleep(3600000); //Runs every hour to check if the user is inactive

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (progressCheck != progress) {
                    progressCheck = progress;
                } else if (!stopThread && notificationStop != 1) {
                    createNotificationChannelInactivity();
                    notificationStop++;
                    SharedPreferences settings = getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("notifications", notificationStop);
                    editor.apply();
                }
            }
        }
    }

    private void createNotificationChannelInactivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel inactivity = new NotificationChannel( //Creates the first and second channel with description, ID, and importance if the OS is above Android 8.0
                    CHANNEL_1_ID,
                    "Inactivity",
                    NotificationManager.IMPORTANCE_HIGH
            );
            inactivity.setDescription("Inactive Channel");

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(inactivity);
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.pedometer_icon)
                .setContentTitle("Inactive")
                .setContentText("More than one hour of inactivity! Take two minutes to walk.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSound)
                .setAutoCancel(true)
                .build();

        notificationManagerCompat.notify(1, notification);
    }
}