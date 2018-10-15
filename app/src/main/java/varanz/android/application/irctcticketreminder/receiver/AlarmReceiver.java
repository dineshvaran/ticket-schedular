package varanz.android.application.irctcticketreminder.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.Charset;

import varanz.android.application.irctcticketreminder.AddActivity;
import varanz.android.application.irctcticketreminder.R;
import varanz.android.application.irctcticketreminder.service.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String receiverName = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(receiverName, "onCreate method started");
        startService(context, intent);
        Log.i(receiverName, "onCreate method ended");

    }

    /**
     * Starts AlarmService
     *
     * @param context context
     */
    private void startService(Context context, Intent pIntent) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra("ticketId", pIntent.getIntExtra("ticketId",-1));
        intent.putExtra("fromStation", pIntent.getStringExtra("fromStation"));
        intent.putExtra("toStation", pIntent.getStringExtra("toStation"));
        intent.putExtra("ticketDescription", pIntent.getStringExtra("ticketDescription"));
        intent.putExtra("journeyDay", pIntent.getIntExtra("journeyDay",-1));
        intent.putExtra("journeyMonth", pIntent.getIntExtra("journeyMonth",-1));
        intent.putExtra("journeyYear", pIntent.getIntExtra("journeyYear",-1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }else{
            context.startService(intent);
        }
    }

}
