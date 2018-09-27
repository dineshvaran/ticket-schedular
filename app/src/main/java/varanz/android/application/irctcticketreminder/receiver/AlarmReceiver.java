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
        Log.d(receiverName, "onCreate method started");

        String channelId="channel_default";
        CharSequence channelName = context.getString(R.string.channel_default);
        String notificationTitle = "Reminder to Book Ticket";
        if(!intent.getStringExtra("fromStation").equals("") &&
                !intent.getStringExtra("toStation").equals("")){
            notificationTitle = intent.getStringExtra("fromStation") +
                    "  ->  " + intent.getStringExtra("toStation");
        }

        Intent serviceIntent = new Intent(context, AlarmService.class);
        context.startService(serviceIntent);

        Intent dismissAlarmIntent = new Intent(context, DismissAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 321,
                dismissAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_train)
                .setContentTitle(notificationTitle)
                .setColor(Color.RED)
                .setContentText(intent.getStringExtra("ticketDescription"))
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.alarm_off, "Stop Alarm", pendingIntent)
                .setChannelId(channelId)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis());


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channelHigh =
                    new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channelHigh);
        }
        notificationManager.notify(context.getResources().getInteger(R.integer.alarm_notification_unique_id),
                notification.build());


        Log.d(receiverName, "onCreate method ended");

    }
}
