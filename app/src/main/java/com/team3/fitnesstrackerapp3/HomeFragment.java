package com.team3.fitnesstrackerapp3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements SensorEventListener, LocationListener {

    public static final String CHANNEL_1_ID = "inactivity";
    public static final String CHANNEL_2_ID = "location";

    private static String MY_PREFS = "Values";
    private static String STEPS_KEY = "Steps";
    public static final int RADIUS = 6371;

    private ProgressBar stepProgress;
    private SensorManager sensorManager;
    private Sensor sensor;
    private int progress = 0;
    private int notificationStopInactivity = 0;
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
    private Uri defaultSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.confident); //Custom sound for notification
    private Context context;
    private volatile boolean stopThread = false;
    private int dailyGoal = 5000;
    private int height = 66;
    private float calories = 0;
    private Button buttonWeeklyProgress;
    private int day = -1;
    private double lat1 = 0;
    private double lat2 = 0;
    private double lon1 = 0;
    private double lon2 = 0;
    private double alt1 = 0;
    private double alt2 = 0;
    private boolean distanceCheck = false;
    private int notificationStopLocation = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        stepProgress = view.findViewById(R.id.progressBar_Step_Count);
        stepCount = view.findViewById(R.id.textView_Step_Count);
        calorieCount = view.findViewById(R.id.textView_Calories);
        calorieProgress = view.findViewById(R.id.progressBar_Calories);
        distanceCount = view.findViewById(R.id.textView_Distance);
        distanceProgress = view.findViewById(R.id.progressBar_Disance);
        buttonWeeklyProgress = view.findViewById(R.id.button_weekly_progress); //Initializes all items from xml file

        SharedPreferences settings = getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        Bundle bundle = getArguments(); //Obtains values from Goal and Weight fragments

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
        distanceProgress.setMax(dailyGoal / 19);
        distanceProgress.setProgress(0);

        float weightCalories = weight / 100;

        if (height < 66) {
            if (weight < 200) {
                calorieProgress.setMax(250);
            } else {
                weightCalories = (dailyGoal / 44) * weightCalories;
                int amount = Math.round(weightCalories);
                calorieProgress.setMax(amount);
            }
        }
        if (height >= 66 && height <= 71) {
            if (weight < 200) {
                calorieProgress.setMax(250);
            } else {
                weightCalories = (dailyGoal / 40) * weightCalories;
                int amount = Math.round(weightCalories);
                calorieProgress.setMax(amount);
            }
        }
        if (height > 71) {
            if (weight < 200) {
                calorieProgress.setMax(250);
            } else {
                weightCalories = (dailyGoal / 36) * weightCalories;
                int amount = Math.round(weightCalories);
                calorieProgress.setMax(amount); //Initializes formula for calories based on height and weights
            }
        }
        calorieProgress.setProgress(0);

        notificationManagerCompat = NotificationManagerCompat.from(context); //Create the NotificationManager

        buttonWeeklyProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("dailyGoalProgress", dailyGoal);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                WeeklyProgressFragment weeklyProgressFragment = new WeeklyProgressFragment();
                weeklyProgressFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_container, weeklyProgressFragment);
                fragmentTransaction.commit(); //Goes to the weekly progress fragment when button is clicked
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE); //Gets the sensor manager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER); //Gets step count sensor

        context = getContext().getApplicationContext();

    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;

        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI); //Registers footsteps
        } else {
            Toast.makeText(getActivity(), R.string.home_fragment_toast_sensor, Toast.LENGTH_LONG).show(); //Will show if phone doesn't have built in sensor
        }

        SharedPreferences settings = getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE); //Gets values saved onto the phone
        lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DAY_OF_YEAR);

        notificationStopLocation = settings.getInt("notificationsLocation", 0);
        day = settings.getInt("day1", 0);
        reset = settings.getFloat("resetCheck", 0);
        notificationStopInactivity = settings.getInt("notifications", 0);
        progressCheck = settings.getInt("progress", 0);

        if (day == 6) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("day1", -1);
        }

        while (today != lastTimeStarted) {
            notificationStopInactivity = 0;
            notificationStopLocation = 0;
            SharedPreferences.Editor editor = settings.edit(); //Stores values into the phone
            editor.putInt("notifications", notificationStopInactivity);
            editor.putInt("notificationsLocation", notificationStopLocation);
            editor.apply();
            break;
        }

        InactivityCheck inactivityCheck = new InactivityCheck();
        new Thread(inactivityCheck).start();

        locationCheck();

        stopThread = false;
    }

    public void onPause() {
        super.onPause();
        running = false; //Stops running once app is paused in activity state
        stopThread = true; //Stops the thread from running
        endGPS(); //stops GPS from running
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

        if (height < 66) {
            calories = Float.valueOf((weightFloat / 4400) * progress); //Gets the value of calories
            calorieProgress.setProgress((int) ((weightFloat / 4400) * (progress))); //Sets progress of meter to the calories
        }

        if (height >= 66 && height <= 71) {
            calories = Float.valueOf((weightFloat / 4000) * progress); //Gets the value of calories
            calorieProgress.setProgress((int) ((weightFloat / 4000) * (progress))); //Sets progress of meter to the calories
        }

        if (height > 71) {
            calories = Float.valueOf((weightFloat / 3600) * progress); //Gets the value of calories
            calorieProgress.setProgress((int) ((weightFloat / 3600) * (progress))); //Sets progress of meter to the calories
        }

        calorieCount.setText(String.format("%, .2f", calories)); //Sets text to the calories and formats to 2 decimal spots

        Float progressFloat = (float) progress;
        Float miles = Float.valueOf(progressFloat / 2200); //Gets the value of distance
        distanceCount.setText(String.format("%, .2f", miles));
        distanceProgress.setProgress((int) (miles * 100));

        while (today != lastTimeStarted) { // Resets the daily step count everyday
            String steps = String.valueOf(progress);
            reset = event.values[0];
            SharedPreferences settings = getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            Map<String, Object> note = new HashMap<>();
            note.put(STEPS_KEY, steps);

            Map<String, Object> resetWeekly = new HashMap<>();
            resetWeekly.put(STEPS_KEY, "0");

            if (day == -1) {

                db.collection("Weekly Progress").document("Day 1").set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });

                db.collection("Weekly Progress").document("Day 2").set(resetWeekly)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });

                db.collection("Weekly Progress").document("Day 3").set(resetWeekly)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });

                db.collection("Weekly Progress").document("Day 4").set(resetWeekly)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });

                db.collection("Weekly Progress").document("Day 5").set(resetWeekly)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });

                db.collection("Weekly Progress").document("Day 6").set(resetWeekly)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });

                db.collection("Weekly Progress").document("Day 7").set(resetWeekly)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), R.string.home_fragment_database, Toast.LENGTH_SHORT).show();
                            }
                        }); //Initializes all of the documents in the app when all seven days passes

            }
            if (day == 0) {

                db.collection("Weekly Progress").document("Day 2").set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), R.string.home_fragment_database, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            if (day == 1) {

                db.collection("Weekly Progress").document("Day 3").set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), R.string.home_fragment_database, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            if (day == 2) {

                db.collection("Weekly Progress").document("Day 4").set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), R.string.home_fragment_database, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            if (day == 3) {

                db.collection("Weekly Progress").document("Day 5").set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), R.string.home_fragment_database, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            if (day == 4) {

                db.collection("Weekly Progress").document("Day 6").set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), R.string.home_fragment_database, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            if (day == 5) {

                db.collection("Weekly Progress").document("Day 7").set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), R.string.home_fragment_database, Toast.LENGTH_SHORT).show();
                            }
                        }); //Checks for the day. If it's a certain day, the steps will be saved to the database for each corresponding document
            }
            day++;
            editor.putInt("day1", day);
            editor.putFloat("resetCheck", reset);
            editor.putInt("last_time_started", today);
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
                    Thread.sleep(20000); //Runs every hour to check if the user is inactive

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (progressCheck != progress) {
                    progressCheck = progress;
                } else if (!stopThread && notificationStopInactivity != 2) { //Notification will be shown if user hasn't moved.
                    createNotificationChannelInactivity();
                    notificationStopInactivity++;
                    SharedPreferences settings = getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("notifications", notificationStopInactivity);
                    editor.apply();
                }
            }
        }
    }

    private void createNotificationChannelInactivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel inactivity = new NotificationChannel( //Creates the first channel with description, ID, and importance if the OS is above Android 8.0
                    CHANNEL_1_ID,
                    "Inactivity",
                    NotificationManager.IMPORTANCE_HIGH
            );
            inactivity.setDescription("Inactive Channel");

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(inactivity); //Initializes the notification channel.
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID) //Initializes all attributes of the notification
                .setSmallIcon(R.drawable.pedometer_icon)
                .setContentTitle("Inactive")
                .setContentText("More than one hour of inactivity! Take two minutes to walk.")
                .setColor(Color.argb(0, 0, 204, 0))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSound)
                .setAutoCancel(true)
                .build();

        notificationManagerCompat.notify(1, notification);
    }

    private void createNotificationChannelLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel location = new NotificationChannel( //Creates the second channel with description, ID, and importance if the OS is above Android 8.0
                    CHANNEL_2_ID,
                    "Location",
                    NotificationManager.IMPORTANCE_HIGH
            );
            location.setDescription("Location Channel");

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(location); //Initializes the notification channel
        }

        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_2_ID) //Initializes all attributes of the notification
                .setSmallIcon(R.drawable.pedometer_icon)
                .setContentTitle("Location")
                .setContentText("Make sure to park farther away to accomplish your goal.")
                .setColor(Color.argb(0, 0, 204, 0))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSound)
                .setAutoCancel(true)
                .build();

        notificationManagerCompat.notify(1, notification);
    }

    public void locationCheck() {
        try {
            locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0, this); //Checks for location using GPS every 6 seconds
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        lat2 = location.getLatitude();
        lon2 = location.getLongitude();
        alt2 = location.getAltitude();

        SharedPreferences settings = getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (!distanceCheck) {
            distanceCheck = true;
        } else {
            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); //Formula to get distance using latitude, longitude, and altitude
            double distance = RADIUS * c * 1000; // convert to meters

            double height = alt1 - alt2;

            distance = Math.pow(distance, 2) + Math.pow(height, 2); //Gets distance using Haversine method

            double speed = ((Math.sqrt(distance) * 0.000621371) / 6) * 3600; //Converts to miles per hour

            if ((lat2 < 40.743) && (lat2 > 40.742) && (lon2 > -73.65) && (lon2 < 73.64) && (speed > 15) && progress < dailyGoal) { //Only puts notification if near the school parking lot and going more than 15mph
                if (notificationStopLocation == 0) {
                    createNotificationChannelLocation();
                    notificationStopLocation++;
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("notificationsLocation", notificationStopLocation);
                }
            }
        }

        lat1 = lat2;
        lon1 = lon2;
        alt1 = alt2; //Initializes the latest attributes to these for the formula
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void endGPS() {

        try {
            locationManager.removeUpdates(this); //ends GPS tracking when app not in function
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}