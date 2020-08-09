package com.pawa.aeroxo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pawa.aeroxo.bd.Track;
import com.pawa.aeroxo.bd.TrackViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private Handler handlerUpdateDatabse = new Handler();
    private FireBase FB;
    final int RC_ACT = 1234;
    final String TAG = "MainLog";
    private FirebaseUser user;
    private TrackViewModel trackViewModel;
    private static final int NOTIFY_ID = 101;
    private FloatingActionButton floatingActionButton;

    // Идентификатор канала
    private static String CHANNEL_ID = "Aeroxo Channel";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Check Authorization
        sharedPreferences = getSharedPreferences("isAuthoraized",Context.MODE_PRIVATE);
        if(sharedPreferences.getInt("auth",0)==0){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivityForResult(intent,RC_ACT);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        //TODO:Включить обновление треков в фоне
        //startService(new Intent(MainActivity.this,Delayed.class));
    }
    private void makeNotify(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_aeroxo)
                        .setContentTitle("Напоминание")
                        .setContentText("Пора зайти в приложение")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Chanel Aeroxo";
            String description = "chanel aeroxo decription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(1, builder.build());
        }

    }

    private void startJob(){
        //TODO:разобраться с временем отложки
        ComponentName componentName = new ComponentName(this, JobUpdateTracks.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                //.setMinimumLatency(1000*60*10)
                .setPeriodic(1000*60*60*6,1000*60*60*6)
                //.setPersisted(true)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    private void stopJob(){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG, "Job cancelled");
    }

}
