package com.pawa.aeroxo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;
import com.pawa.aeroxo.bd.Database;
import com.pawa.aeroxo.bd.Track;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;


public class JobUpdateTracks extends JobService {

    private static final String TAG = "JobUpdate";
    private static String CHANNEL_ID = "Aeroxo Channel";

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Database db = Room.databaseBuilder(getApplicationContext(),Database.class,"database").build();
                FireBase fireBase = new FireBase();
                List<Track>  tracks = db.trackDao().getAll();
                Log.d(TAG, "In Job: get "+String.valueOf(tracks.size())+" tracks");
                db.trackDao().deleteAll();
                int tracksIsUptoDate = 0;
                for (Track track:tracks){
                    Track24 getInformTrack = new Track24();
                    String result = "";
                    getInformTrack.execute("https://api.track24.ru/tracking.json.php?apiKey=b03370759b96d56d48d0541e9402e86e&pretty=true&domain=demo.track24.ru&lng=en&code="+track.trackNumber);
                    try{
                        result = getInformTrack.get();
                        Log.d(TAG, "result by track"+track.trackNumber);
                        tracksIsUptoDate++;
                        if(!result.equals(track.status)){
                            makeNotify(track.Name,result,track.trackNumber,tracksIsUptoDate);
                        }
                        track.status = result;
                        db.trackDao().insert(track);
                        Log.d(TAG,result);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }
                Log.d(TAG, "Update Tracks");
                makeNotify("Обновлено","Статусы треков обновлены","Всего "+String.valueOf(tracks.size())+" трека(-ов)",100);
                jobFinished(jobParameters,true);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob: ");
        return true;
    }
    private void makeNotify(String title,String message,String bigMessage,int id){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_aeroxo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(bigMessage))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Track Notification";
            String description = "Track  notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(id, builder.build());
        }

    }
}
