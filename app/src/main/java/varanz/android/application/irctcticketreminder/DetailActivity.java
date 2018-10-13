package varanz.android.application.irctcticketreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import varanz.android.application.irctcticketreminder.receiver.AlarmReceiver;
import varanz.android.application.irctcticketreminder.store.TicketSchedularDataBase;
import varanz.android.application.irctcticketreminder.store.TicketSchedularEntity;

public class DetailActivity extends AppCompatActivity {

    /**
     * Views in detail
     */
    TextView ticketDescription;
    TextView fromStation;
    TextView toStation;
    TextView journeyDate;
    TextView reminderDate;
    TextView reminderTime;

    /**
     * used for CRUD operation
     */
    TicketSchedularDataBase database;

    /**
     * ticket Id
     */
    int ticketId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        afterOnCreate();
    }

    /**
     * initializes and set data during activity creation.
     */
    private void afterOnCreate() {
        database = Room.databaseBuilder(getApplicationContext(),
                TicketSchedularDataBase.class, TicketSchedularEntity.class.getSimpleName())
                .allowMainThreadQueries().build();
        ticketId = getIntent().getIntExtra("ticketId", -1);
        initialize();
        setData();
    }

    /**
     * initializes views in detail activity
     */
    private void initialize() {
        ticketDescription = findViewById(R.id.ticket_description);
        fromStation = findViewById(R.id.from_station);
        toStation = findViewById(R.id.to_station);
        journeyDate = findViewById(R.id.journey_date);
        reminderDate = findViewById(R.id.reminder_date);
        reminderTime = findViewById(R.id.reminder_time);
    }

    /**
     * gets information from db for the given ticket id.
     *
     * @param ticketId ticketId
     * @return TicketSchedularEntity
     */
    private TicketSchedularEntity getTicketDetails(int ticketId) {
        return database.getTicketSchedularDao().getTicketDetail(ticketId);
    }

    /**
     * sets data to views
     */
    private void setData() {
        TicketSchedularEntity entity = getTicketDetails(ticketId);
        ticketDescription.setText(entity.getTicetDescription());
        fromStation.setText(entity.getFromStation());
        toStation.setText(entity.getToStation());
        journeyDate.setText(formatDate(entity.getJourneyDate().getTime()));
        reminderDate.setText(formatDate(entity.getBookingDate().getTime()));
        reminderTime.setText(String.format(Locale.ENGLISH, "%1$02d : %2$02d", entity.getReminderHour(), entity.getReminderMinute()));
    }

    /**
     * formats the given date in the given format
     *
     * @param date   Date
     * @param format string
     * @return string
     */
    private String formatDate(Date date, @Nullable String format) {
        if (TextUtils.isEmpty(format)) {
            format = "MMM dd, yyyy";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        return dateFormat.format(date);
    }

    /**
     * overloaded method for formatDate(date, format), use this method for optional format.
     * @param date Date
     * @return String
     */
    private String formatDate(Date date){
        return formatDate(date,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case (R.id.action_delete):
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_delete_red_24dp)
                        .setTitle("Confirm Delete")
                        .setMessage("Reminder will be deleted permanently.")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(AddActivity.ALARM_SERVICE);
                                Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                                PendingIntent pendingAlarmIntent =
                                        PendingIntent.getBroadcast(getApplicationContext(), ticketId, alarmIntent, 0);
                                alarmManager.cancel(pendingAlarmIntent);
                                database.getTicketSchedularDao().deleteTicketDetailByTicketId(ticketId);
                                finish();
                            }
                        })
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }
}
