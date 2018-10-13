package varanz.android.application.irctcticketreminder.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import varanz.android.application.irctcticketreminder.DetailActivity;
import varanz.android.application.irctcticketreminder.MainActivity;
import varanz.android.application.irctcticketreminder.R;
import varanz.android.application.irctcticketreminder.receiver.DismissAlarmReceiver;

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (null != alarmUri) {
            ringtone = RingtoneManager.getRingtone(this, alarmUri);
            ringtone.play();
            createNotification(intent);
        }
        startForeground(intent.getIntExtra("ticketId", -1), createNotification(intent));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(serviceName, "onDestroy method started");
        super.onDestroy();
        if (null != ringtone) {
            ringtone.stop();
        }
        Log.i(serviceName, "onDestroy method ended");
    }

    /**
     * creates Notification for the ongoiong service.
     *
     * @param intent intent
     */
    private Notification createNotification(Intent intent) {
        String channelId = "channel_default";
        String notificationTitle = "Reminder to Book Ticket";
        String fromStation = intent.getStringExtra("fromStation");
        String toStation = intent.getStringExtra("toStation");
        String journeyDate = intent.getStringExtra("journeyDate");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        try {
            journeyDate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy")
                    .parse(intent.getStringExtra("journeyDate")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int ticketId = intent.getIntExtra("ticketId", -1);
        CharSequence channelName = getApplicationContext().getString(R.string.channelNameHigh);

        if (!fromStation.equals("") && !toStation.equals("")) {
            notificationTitle = fromStation + "  >  " + toStation;
        }

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (null == notificationManager) {
            return null;
        }

        Intent dismissAlarmIntent = new Intent(getApplicationContext(), DismissAlarmReceiver.class);
        dismissAlarmIntent.putExtra("ticketId", ticketId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 321,
                dismissAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent notificationDetailIntent = new Intent(getApplicationContext(), DetailActivity.class);
        notificationDetailIntent.putExtra("ticketId", ticketId);
        PendingIntent notificationDetailPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), ticketId,
                        notificationDetailIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_train)
                .setContentTitle(notificationTitle)
                .setColor(Color.RED)
                .setContentText("Journey date is " + journeyDate)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.alarm_off, "Off Alarm", pendingIntent)
                .setChannelId(channelId)
                .setOngoing(true)
                .setContentIntent(notificationDetailPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        Html.fromHtml("<big>Journey date is "+journeyDate+"</big><br>"
                                + intent.getStringExtra("ticketDescription"))))
                .setWhen(System.currentTimeMillis());


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channelHigh =
                    new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channelHigh);
        }

        return notification.build();
    }
}

