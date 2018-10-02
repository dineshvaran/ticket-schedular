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
        stopService(context);
        dismissNotification(context,intent);
        Log.i(className, "onReceive method ends");
    }

    /**
     * stops AlarmService
     * @param context context
     */
    private void stopService(Context context){
        Log.i(className, "stopService method starts");
        context.stopService(new Intent(context, AlarmService.class));
        Log.i(className, "stopService method ends");
    }

    /**
     * Dismisses ongoing notification
     * @param context context
     * @param intent intent
     */
    private void dismissNotification(Context context, Intent intent){
        Log.i(className, "dismissNotification method starts");
        int ticketId=intent.getIntExtra("ticketId",-1);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(ticketId);
        }
        Log.i(className, "dismissNotification method ends");
    }

}
