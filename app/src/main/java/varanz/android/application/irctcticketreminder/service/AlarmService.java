package varanz.android.application.irctcticketreminder.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import varanz.android.application.irctcticketreminder.R;

public class AlarmService extends Service {

    String serviceName = AlarmService.class.getSimpleName();
    Uri alarmUri;
    Ringtone ringtone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(serviceName, "onCreate method started");
        super.onCreate();

        alarmUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(null!=alarmUri){
            ringtone=RingtoneManager.getRingtone(this,alarmUri);
            ringtone.play();
        }
        Log.i(serviceName, "onCreate method ended");
    }

    @Override
    public void onDestroy() {
        Log.i(serviceName, "onDestroy method started");
        super.onDestroy();
        if(null!=ringtone){
            ringtone.stop();
        }
        Log.i(serviceName, "onDestroy method ended");
    }
}
