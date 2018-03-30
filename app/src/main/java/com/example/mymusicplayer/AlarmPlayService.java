package com.example.mymusicplayer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmPlayService extends Service {
    private final static String TAG = "AlarmPlayService";
    private long startTime;
    private long endTime;
    private Intent serviceIntent;
    private PendingIntent pen;
    public AlarmPlayService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        endTime = intent.getLongExtra("endTime",-1);
        Log.d(TAG, "onStartCommand: startTime=" + startTime+"endTime=" + endTime);
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        endTime = SystemClock.elapsedRealtime() + (endTime-startTime)*60*1000;
        Intent intent1 = new Intent(this,AlarmPlayService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent1,0);
        new Thread(()->{

        }).start();
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,endTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serviceIntent = new Intent(this,AlarmPlayService.class);
        pen = PendingIntent.getActivity(this,0,serviceIntent,0);
        Notification notification = new Notification.Builder(this)
                .setContentText("正在计时"+endTime)
                .setContentIntent(pen)
                .setContentTitle("定时服务").build();
        startForeground(1,notification);
        startTime = System.currentTimeMillis();
        Log.d(TAG, "onCreate: startTime=" +startTime);
    }

    @Override
    public IBinder onBind(Intent intent) {
        startTime = intent.getLongExtra("startTime",-1);

        endTime = intent.getLongExtra("endTime",-1);

        Log.d(TAG, "onBind: endTime=" + endTime);
        return null;
    }

}
