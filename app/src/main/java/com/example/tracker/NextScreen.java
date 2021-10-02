package com.example.tracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class NextScreen extends AppCompatActivity implements SensorEventListener {
    private static final int SENSOR_SENSITIVITY = 8;
    protected final String TAG = getClass().getSimpleName();
    private final Intent callIntent = new Intent(Intent.ACTION_CALL);
    Intent service;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private String PhoneNumber;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PhoneNumber = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_screen);
        super.onStart();
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                PhoneNumber = null;
            } else {
                PhoneNumber = extras.getString("PhoneNum");
            }

        } else {
            PhoneNumber = (String) savedInstanceState.getSerializable("PhoneNum");
        }
        Log.d(TAG, PhoneNumber);

        //////////////////////////Automatically Camera Code///////////////////////
        /*Calendar cal = Calendar.getInstance();

        service = new Intent(getBaseContext(), CapPhoto.class);
        cal.add(Calendar.SECOND, 15);
        //TAKE PHOTO EVERY 15 SECONDS
        PendingIntent pintent = PendingIntent.getService(this, 0, service, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                60 * 60 * 1000, pintent);
        startService(service);*/
        /////////////////// Proximty Sensor  ///////////////////
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        /*
        Sensor Maximum Range Checking
        Sensor proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Log.d(TAG, "SensorRange:"+String.valueOf(proximitySensor.getMaximumRange()));*/
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
        //p
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);
        //onResume();
        //p
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if (mAccel > 2) {
                Log.d(TAG, "Device motion detected!!!!");
                if (PhoneNumber != null) {
                    CallPhone();
                }

            }
        }
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                //near

                Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();
            } else {
                //far
                Calendar cal = Calendar.getInstance();

                service = new Intent(getBaseContext(), CapPhoto.class);
                cal.add(Calendar.SECOND, 15);
                //TAKE PHOTO EVERY 15 SECONDS
                PendingIntent pintent = PendingIntent.getService(this, 0, service, 0);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                        60 * 60 * 1000, pintent);
                startService(service);
                //Toast

                Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //Camera Function
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nextscreen, menu);
        return true;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // required method
        Log.d(TAG, sensor.getName() + "NK:" + accuracy);

    }

    public void CallPhone() {
        Toast.makeText(getApplicationContext(), "Phone Call Initiated", Toast.LENGTH_LONG).show();
        callIntent.setData(Uri.parse("tel:" + PhoneNumber.trim()));
        startActivity(callIntent);
    }
}