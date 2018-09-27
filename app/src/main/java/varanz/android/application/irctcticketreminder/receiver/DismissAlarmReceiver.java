package varanz.android.application.irctcticketreminder.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import varanz.android.application.irctcticketreminder.R;
import varanz.android.application.irctcticketreminder.service.AlarmService;

public class DismissAlarmReceiver extends BroadcastReceiver {

    private String className = DismissAlarmReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(className, "onReceive method starts");

        Intent serviceIntent = new Intent(context, AlarmService.class);
        context.stopService(serviceIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel(context.getResources().getInteger(R.integer.alarm_notification_unique_id));
        Log.i(className, "onReceive method ends");
    }
}
