package com.team3.fitnesstrackerapp3;

import android.app.Notification;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static com.team3.fitnesstrackerapp3.NotificationChannels.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
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
    private int date;
    private int dateCheck;
    private ProgressBar calorieProgress;
    private EditText weightInput;
    private TextView distanceCount;
    private ProgressBar distanceProgress;
    private int progressReset = 0;
    private int progressCheck = 0;
    private NotificationManagerCompat notificationManagerCompat;
    private Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide(); //hide the title bar

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        stepProgress = findViewById(R.id.progressBar_Step_Count); //Gets progress bar from activity_main.xml
        stepCount = findViewById(R.id.textView_Step_Count);
        calorieCount = findViewById(R.id.textView_Calories);
        calorieProgress = findViewById(R.id.progressBar_Calories);
        weightInput = findViewById(R.id.editText_Weight_Change);
        distanceCount = findViewById(R.id.textView_Distance);
        distanceProgress = findViewById(R.id.progressBar_Disance);

        stepProgress.setMax(5000);
        stepProgress.setProgress(0);
        calorieProgress.setMax(1000);
        calorieProgress.setProgress(0);
        distanceProgress.setMax(500);
        distanceProgress.setProgress(0);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); //Gets the sensor manager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER); //Gets step count sensor

        date = (Calendar.DATE);

        notificationManagerCompat = NotificationManagerCompat.from(this); //Create the NotificationManager

        progressCheck = progress;

        t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(5000); //Runs every 5 seconds to check date

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dateCheck = Calendar.DATE;
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();

        inactivity = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(600000); //Runs every 5 seconds to check date

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                inactivityNotification();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        inactivity.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;

        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI); //Registers footsteps
        } else {
            Toast.makeText(this, "No sensor detected. App needs sensor. Sorry.", Toast.LENGTH_LONG).show(); //Will show if phone doesn't have built in sensor
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false; //Stops running once app is paused in activity state
        progressReset = progress; //Saves the progress once the app is not in focus
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            progress = (int) (event.values[0] - reset);
        }

        stepCount.setText(String.valueOf(progress)); //Shows the count of the steps everyday
        stepProgress.setProgress(progress);

        if (date != dateCheck) { //If date is not the same, reset the progress
            reset = event.values[0]; //Will reset progress in the progress circle
            date = dateCheck;
        }

        Float calories = Float.valueOf(((weight / 4000) * progress)); //Gets the value of calories
        calorieCount.setText(String.format("%, .2f", calories)); //Sets text to the calories and formats to 2 decimal spots
        calorieProgress.setProgress((int) ((weight / 4000) * (progress))); //Sets progress of meter to the calories

        Float progressFloat = (float) progress;
        Float miles = Float.valueOf(progressFloat / 2200); //Gets the value of distance
        distanceCount.setText(String.format("%, .2f", miles));
        distanceProgress.setProgress((int) (miles * 100));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void weightChange(View view) {
        if (weightInput.getText().toString() == "") {
            Toast.makeText(this, "Please Input a number", Toast.LENGTH_SHORT);
        } else {
            weight = Float.parseFloat(weightInput.getText().toString()); //When the button is pressed, the weight value is changed

            Float calories = Float.valueOf(((weight / 4000) * progress)); //Gets the value of calories
            calorieCount.setText(String.format("%, .2f", calories)); //Sets text to the calories and formats to 2 decimal spots
            calorieProgress.setProgress((int) ((weight / 4000) * (progress))); //Sets progress of meter to the calories
        }
    }

    public void inactivityNotification() {
        if (progressCheck == progress) { //Creates the notification if the amount of steps hasn't changed for an hour
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.pedometer_icon)
                    .setContentTitle("Inactive")
                    .setContentText("You've been inactive for more than one hour.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSound(defaultSound)
                    .build();

            notificationManagerCompat.notify(1, notification);
        }
        else {
            progressCheck = progress; //Makes them equal so that the check can happen again
        }
    }
}