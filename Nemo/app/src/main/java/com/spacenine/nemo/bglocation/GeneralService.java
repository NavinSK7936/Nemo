package com.spacenine.nemo.bglocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.spacenine.nemo.AppKt;
import com.spacenine.nemo.ContactModel;
import com.spacenine.nemo.DbHelper;
import com.spacenine.nemo.MainActivity;
import com.spacenine.nemo.R;
import com.spacenine.nemo.SensorService;

import java.util.List;

import kotlin.jvm.Synchronized;

//public class GeneralService extends IntentService {
//
//    public volatile static String phoneNumber = null;
//
//    public GeneralService() {
//        super("General Service");
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//
//        return START_STICKY;
//    }
//
//    @SuppressLint("MissingPermission")
//    private final Thread locationUpdatingThread = new Thread(() -> {
//
//        while (true) {
//            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//                return;
//
//            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, new CancellationToken() {
//                @Override
//                public boolean isCancellationRequested() {
//                    return false;
//                }
//
//                @NonNull
//                @Override
//                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
//                    return null;
//                }
//            }).addOnSuccessListener(new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if (phoneNumber == null)
//                        return;
//                    Log.e("HIIIIIIIIIIIIIIIIIIIII", location.toString());
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d("Check: ", "OnFailure");
//                }
//            });
//        }
//    });
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        locationUpdatingThread.start();
//
//
//    }
//}


//public class GeneralService extends Service {
//    public static final String CHANNEL_ID = "ForegroundServiceChannel";
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        String input = intent.getStringExtra("inputExtra");
//        createNotificationChannel();
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Foreground Service")
//                .setContentText(input)
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);
//
//        //do heavy work on a background thread
//
//        new Thread(() -> {
//
//            try {
//                for (int i = 0; i < 10; i++) {
//                    AppKt.runOnUIThread(() -> {
//                        Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();
//                    });
//                    Thread.sleep(3000);
//                }
//            } catch (Exception e) {
//                AppKt.runOnUIThread(() -> {
//                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                });
//            }
//
//        }).start();
//
//
//        //stopSelf();
//
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Foreground Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//    }
//
//}
