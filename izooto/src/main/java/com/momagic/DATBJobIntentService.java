package com.momagic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;

public class DATBJobIntentService extends JobIntentService {
    private static int JOB_ID = 1000;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
//        Log.e("iZootoData", "onHandleWork ");
        setAlarm();
        stopSelf();
    }
    public static void enqueueWork(Context ctx) {
        Intent intent = new Intent(ctx, DATBJobIntentService.class);
        enqueueWork(ctx, DATBJobIntentService.class, JOB_ID, intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setAlarm() {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT==Build.VERSION_CODES.KITKAT){
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);
        }else if ((Build.VERSION.SDK_INT<=Build.VERSION_CODES.M) && !(Build.VERSION.SDK_INT==Build.VERSION_CODES.KITKAT)) {
            alarmService.setExact(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);
        }
        else{
            alarmService.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.e("setAlarmCtx", "onReceive --onDestroy ");
        Intent broadcastIntent = new Intent(this, DATBBackgroundReceiver.class);
        sendBroadcast(broadcastIntent);
    }
}
