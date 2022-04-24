package com.spacenine.nemo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.spacenine.nemo.gravity.ForceDetector;
import com.spacenine.nemo.gravity.ForceDetectorKt;

import java.util.List;
import static com.spacenine.nemo.AppKt.*;

public class SensorService extends Service {

    @SuppressLint("MissingPermission")
    private void doSend(String message0) {

        // vibrate the phone
        vibrate();

        // create FusedLocationProviderClient to get the user location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        // use the PRIORITY_BALANCED_POWER_ACCURACY
        // so that the service doesn't use unnecessary power via GPS
        // it will only use GPS at this very moment
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                Toast.makeText(SensorService.this, "MESSAGE SENT", Toast.LENGTH_SHORT).show();

                SmsManager smsManager = SmsManager.getDefault();
                DbHelper db = new DbHelper(SensorService.this);
                List<ContactModel> list = db.getAllContacts();

                String message;

                if (location != null) {
                    for (ContactModel c : list) {
                        message = "Hey, " + c.getName() + " " + message0 + " Please urgently reach me out. Here are my coordinates.\n" + "http://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                        smsManager.sendTextMessage(c.getPhoneNo(), null, message, null, null);
                    }
                } else {
                    for (ContactModel c : list) {
                        message = "Hey, " + c.getName() + " " + message0 + " Please urgently reach me out.\n" + "GPS was turned off. Couldn't find location. Call your nearest Police Station.";
                        smsManager.sendTextMessage(c.getPhoneNo(), null, message, null, null);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Check: ", "OnFailure");
                String message = "Hey, " + message0 + " Please urgently reach me out.\n" + "GPS was turned off.Couldn't find location. Call your nearest Police Station.";

                SmsManager smsManager = SmsManager.getDefault();
                DbHelper db = new DbHelper(SensorService.this);
                List<ContactModel> list = db.getAllContacts();

                for (ContactModel c : list) {
                    smsManager.sendTextMessage(c.getPhoneNo(), null, message, null, null);
                }
            }
        });

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        // start the foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        // ShakeDetector initialization
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // check if the user has shacked
        // the phone for 3 time in a row
        ShakeDetector mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onShake(int count) {
                // check if the user has shacked
                // the phone for 3 time in a row
                if (count == MAX_SHAKE) {
                    doSend(ON_SHAKE_MESSAGE);
                }
            }
        });
        ForceDetector mForceDetector = new ForceDetector(new ForceDetector.ForceListener() {
            @Override
            public void onHit(float gX, float gY, float gZ) {
                double netG = ForceDetectorKt.getNetForce(gX, gY, gZ);

                if (netG >= MAX_NET_G_VALUE) {
                    doSend(ON_HIT_MESSAGE);
                }

            }
        });

        // register the listener
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mForceDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    // method to vibrate the phone
    public void vibrate() {

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Android Q and above have some predefined vibrating patterns
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            VibrationEffect vibEff = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK);
            vibrator.cancel();
            vibrator.vibrate(vibEff);
        } else {
            vibrator.vibrate(500);
        }


    }

    // For Build versions higher than Android Oreo, we launch
    // a foreground service in a different way. This is due to the newly
    // implemented strict notification rules, which require us to identify
    // our own notification channel in order to view them correctly.
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("You are protected.")
                .setContentText("We are there for you")

                // this is important, otherwise the notification will show the way
                // you want i.e. it will show some default notification
                .setSmallIcon(R.drawable.ic_launcher_foreground)

                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public void onDestroy() {
        // create an Intent to call the Broadcast receiver
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ReactivateService.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

}
