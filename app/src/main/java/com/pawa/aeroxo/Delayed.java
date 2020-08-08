package com.pawa.aeroxo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class Delayed extends Service {
    public Delayed() {
    }

    @Override
    public void onDestroy() {
        Log.d("Service","Destroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service","Start Command");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service","Create");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("Service","onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
