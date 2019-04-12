package com.team3.fitnesstrackerapp3;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static com.team3.fitnesstrackerapp3.NotificationChannels.CHANNEL_1_ID;

public class HomeFragment extends Fragment implements SensorEventListener {
    private ProgressBar stepProgress;
    private SensorManager sensorManager;
    private Sensor sensor;
    private int progress = 0;
    private float weight = 100;
    private TextView stepCount;
    private TextView calorieCount;
    private boolean running = true;
    private float reset = 0;
    private Thread t;
    private Thread inactivity;
    private int today = 0;
    private int lastTimeStarted = 0;
    private ProgressBar calorieProgress;
    private EditText weightInput;
    private TextView distanceCount;
    private ProgressBar distanceProgress;
    private int progressReset = 0;
    private int progressCheck = 0;
    private NotificationManagerCompat notificationManagerCompat;
    private Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private SharedPreferences settings;


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

        stepProgress.setMax(5000);
        stepProgress.setProgress(0);
        calorieProgress.setMax(1000);
        calorieProgress.setProgress(0);
        distanceProgress.setMax(500);
        distanceProgress.setProgress(0);

        notificationManagerCompat = NotificationManagerCompat.from(getActivity()); //Create the NotificationManager

        progressCheck = progress;

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE); //Gets the sensor manager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER); //Gets step count sensor

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

        settings  = PreferenceManager.getDefaultSharedPreferences(getActivity());
        lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DAY_OF_YEAR);

        if(today!= lastTimeStarted) {

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("last_time_started", today);
            editor.commit();
        }
    }

    public void onPause() {
        super.onPause();
        running = false; //Stops running once app is paused in activity state
        progressReset = progress; //Saves the progress once the app is not in focus

    }

    public void onSensorChanged(SensorEvent event) {
        if (running) {
            progress = (int) (event.values[0] - reset);
        }

        stepCount.setText(String.valueOf(progress)); //Shows the count of the steps everyday
        stepProgress.setProgress(progress);

        if (lastTimeStarted!= lastTimeStarted) { //If date is not the same, reset the progress
            reset = event.values[0]; //Will reset progress in the progress circle

        }

        Float calories = Float.valueOf(((weight / 4000) * progress)); //Gets the value of calories
        calorieCount.setText(String.format("%, .2f", calories)); //Sets text to the calories and formats to 2 decimal spots
        calorieProgress.setProgress((int) ((weight / 4000) * (progress))); //Sets progress of meter to the calories

        Float progressFloat = (float) progress;
        Float miles = Float.valueOf(progressFloat / 2200); //Gets the value of distance
        distanceCount.setText(String.format("%, .2f", miles));
        distanceProgress.setProgress((int) (miles * 100));

        progressReset = progress; //Saves the progress once the app is not in focus
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void inactivityNotification() {
        if (progressCheck == progress) { //Creates the notification if the amount of steps hasn't changed for an hour
            Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.pedometer_icon)
                    .setContentTitle("Inactive")
                    .setContentText("More than one hour of inactivity! Take two minutes to walk.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSound(defaultSound)
                    .build();

            notificationManagerCompat.notify(1, notification);
        } else {
            progressCheck = progress; //Makes them equal so that the check can happen again
        }
    }
}
