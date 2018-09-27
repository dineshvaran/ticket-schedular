package varanz.android.application.irctcticketreminder.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import varanz.android.application.irctcticketreminder.R;

public class AlarmService extends Service {

    String serviceName = AlarmService.class.getSimpleName();
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(serviceName, "onCreate method started");
        super.onCreate();

        //Start media player
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        Log.d(serviceName, "onCreate method ended");
    }

    @Override
    public void onDestroy() {
        Log.d(serviceName, "onDestroy method started");
        super.onDestroy();

        //On destory stop and release the media player
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        Log.d(serviceName, "onDestroy method ended");
    }
}
