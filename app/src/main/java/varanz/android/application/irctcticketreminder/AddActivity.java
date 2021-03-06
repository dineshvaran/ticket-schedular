package varanz.android.application.irctcticketreminder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import varanz.android.application.irctcticketreminder.receiver.AlarmReceiver;
import varanz.android.application.irctcticketreminder.store.TicketSchedularDataBase;
import varanz.android.application.irctcticketreminder.store.TicketSchedularEntity;

public class AddActivity extends AppCompatActivity {

    private final String className = AddActivity.class.getSimpleName();
    private static final int INT_DEFAULT_VALUE = -1;
    private static final int CALENDAR_PERMISSON = 1;

    /**
     * used to start service (stores date and time in Millis)
     */
    private long alarmTime;

    // selected date, month and year
    private int sDate = INT_DEFAULT_VALUE;
    private int sMonth = INT_DEFAULT_VALUE;
    private int sYear = INT_DEFAULT_VALUE;

    private int sHour = INT_DEFAULT_VALUE;
    private int sMinute = INT_DEFAULT_VALUE;

    // booking date, month and year
    private int bDate = INT_DEFAULT_VALUE;
    private int bMonth = INT_DEFAULT_VALUE;
    private int bYear = INT_DEFAULT_VALUE;

    /**
     * Views in this activity
     */
    private Button datePickerButton;
    private EditText ticketDescription;
    private EditText fromStation;
    private EditText toStation;
    private Button timePickerButton;
    private RadioGroup alarmRadioGroup;
    private MaterialCardView materialCardView;
    private TextView bookingDateText;

    /**
     * used to handle the active ticket
     */
    boolean isRecordSaved = false;

    // value is 0, when no empty record is inserted.
    int ticketId = 0;

    /**
     * used for CRUD operation
     */
    TicketSchedularDataBase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initializeViewObjects();
        addListeners();
        database = Room.databaseBuilder(getApplicationContext(),
                TicketSchedularDataBase.class, TicketSchedularEntity.class.getSimpleName())
                .allowMainThreadQueries().build();

        insertEmptyData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (validateInputs() && validateSelectedDate() && validateBookingDate() && validateSelectedTime()) {
            int radioItemId = alarmRadioGroup.getCheckedRadioButtonId();
            RadioButton radioItem = findViewById(radioItemId);
            if (radioItem.getText().toString().equals(getString(R.string.alarm_radio_item))) {
                saveData(getString(R.string.alarm_radio_item));
                setAlarm(alarmTime);
                Toast.makeText(this, getString(R.string.toast_success_message), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                checkCalendarPermisson();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkCalendarPermisson() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
        pushEventToCalender();
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, CALENDAR_PERMISSON);
//        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALENDAR_PERMISSON:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pushEventToCalender();
                } else {
                    Toast.makeText(this, "Give Calendar Permission to Add Reminder", Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Add event to calendar.
     */
    @SuppressLint("MissingPermission")
    public void pushEventToCalender() {
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        beginTime.set(bYear, bMonth, bDate, sHour, sMinute);
        endTime.set(bYear, bMonth, bDate, sHour, (sMinute + 30));

        saveData(getString(R.string.add_to_calendar_item_value));

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, "Book IRCTC Ticket for " + ticketDescription.getText())
                .putExtra(CalendarContract.Events.DESCRIPTION, "Ticket from " + fromStation.getText().toString()
                        + " to " + toStation.getText().toString())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "www.irctc.co.in");
        startActivity(intent);

//        ContentResolver contentResolver = getContentResolver();
//        ContentValues values = new ContentValues();
//        values.put(CalendarContract.Events.CALENDAR_ID, 1);
//        values.put(CalendarContract.Events.TITLE, "Book IRCTC Ticket for " + ticketDescription.getText());
//        values.put(CalendarContract.Events.DESCRIPTION,
//                "Ticket from " + fromStation.getText().toString() + " to " + toStation.getText().toString());
//        values.put(CalendarContract.Events.EVENT_LOCATION, "www.irctc.co.in");
//        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
//        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
//        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
//        contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
//        eventId = Long.parseLong(uri.getLastPathSegment());
//        Toast.makeText(this, "Event added to Calendar", Toast.LENGTH_LONG).show();
    }

    /**
     * Updates the empty ticket in Database
     *
     * @param reminderType enum ReminderType
     */
    private void saveData(String reminderType) {

        Calendar journey = Calendar.getInstance();
        journey.set(sYear, sMonth, sDate);

        Calendar booking = Calendar.getInstance();
        booking.set(bYear, bMonth, bDate);

        TicketSchedularEntity entity = new TicketSchedularEntity();
        entity.setTicketId(ticketId);
        entity.setTicetDescription(ticketDescription.getText().toString());
        entity.setFromStation(fromStation.getText().toString());
        entity.setToStation(toStation.getText().toString());
        entity.setReminderType(reminderType);
        entity.setJourneyDate(journey);
        entity.setBookingDate(booking);

        database.getTicketSchedularDao().updateTicketDetail(entity);
        isRecordSaved = true;
    }


    /**
     * Inserts an empty record in the database
     */
    private void insertEmptyData() {
        TicketSchedularEntity entity = new TicketSchedularEntity();
        ticketId = (int) database.getTicketSchedularDao().insertTicketDetail(entity);
    }

    @Override
    protected void onDestroy() {
        deleteEmptyData();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        deleteEmptyData();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRecordSaved) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteEmptyData();
    }

    /**
     * deletes empty data when record is not saved.
     */
    private void deleteEmptyData() {

        if (!isRecordSaved) {
            database.getTicketSchedularDao().deleteTicketDetailByTicketId(ticketId);
        }
    }

    /**
     * Initializes the Views in this activity
     */
    private void initializeViewObjects() {
        datePickerButton = findViewById(R.id.date_picker);
        ticketDescription = findViewById(R.id.ticket_description);
        fromStation = findViewById(R.id.from_station);
        toStation = findViewById(R.id.to_station);
        timePickerButton = findViewById(R.id.time_picker_button);
        alarmRadioGroup = findViewById(R.id.alarm_type);
        materialCardView = findViewById(R.id.material_card_view);
        bookingDateText = findViewById(R.id.booking_date_text);
    }

    /**
     * Validates the user Input
     *
     * @return boolean
     */
    private boolean validateInputs() {
        if (TextUtils.isEmpty(ticketDescription.getText())) {
            ticketDescription.setError(getString(R.string.toast_enter_description));
            return false;
        } else {
            ticketDescription.setError(null);
        }

        if (TextUtils.isEmpty(fromStation.getText())) {
            fromStation.setError(getString(R.string.toast_enter_from_station));
            return false;
        } else {
            fromStation.setError(null);
        }

        if (TextUtils.isEmpty(toStation.getText())) {
            toStation.setError(getString(R.string.toast_enter_to_station));
            return false;
        } else {
            toStation.setError(null);
        }

        return true;
    }

    /**
     * validates whether selected date is less than the present date
     *
     * @return boolean
     */
    private boolean validateSelectedDate() {
        boolean result;

        if (sYear == INT_DEFAULT_VALUE) {
            Toast.makeText(this, getString(R.string.selectJourneyDate), Toast.LENGTH_LONG).show();
            return false;
        }
        Calendar today = Calendar.getInstance();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(sYear, sMonth, sDate);
        result = selectedDate.getTime().after(today.getTime());
        if (!result)
            Toast.makeText(this, getString(R.string.selectedDatePastError), Toast.LENGTH_LONG).show();
        return result;

    }

    private boolean validateSelectedTime() {
        if (sHour == INT_DEFAULT_VALUE) {
            Toast.makeText(this, getString(R.string.selectedTimePastError), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * validates whether booking date is less than the present date
     *
     * @return boolean
     */
    private boolean validateBookingDate() {
        boolean result = false;
        Calendar today = Calendar.getInstance();
        Calendar bookingDate = Calendar.getInstance();
        bookingDate.set(bYear, bMonth, bDate);
//        result = bookingDate.getTime().after(today.getTime());

        int iresult = bookingDate.getTime().compareTo(today.getTime());
        if (iresult >= 0) {
            result = true;
        }

        if (!result)
            Toast.makeText(this, getString(R.string.bookingDatePastError), Toast.LENGTH_LONG).show();
        return result;
    }

    /**
     * Listens to the event of Views
     */
    private void addListeners() {
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) AddActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                View view = AddActivity.this.getCurrentFocus();
                if (null == view) {
                    view = new View(AddActivity.this);
                }
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                showDialog("datePicker").show();
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog("timePicker").show();
            }
        });
    }

    /**
     * Returns the Calendar Object in Milli Seconds
     *
     * @param date   month date
     * @param month  month
     * @param year   year
     * @param hour   hour
     * @param minute minute
     * @return long
     */
    private long getReminderinMillis(int date, int month, int year, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hour, minute, 0);
        Log.i(className, "Millis to date is " + calendar.getTime().toString());
        Log.i(className, "millis is " + calendar.getTimeInMillis());
        Log.i(className, "system millis is " + System.currentTimeMillis());
        return calendar.getTimeInMillis();
    }

    /**
     * Activates the Media service on the given time
     *
     * @param remindTime when to remind in millis
     */
    private void setAlarm(long remindTime) {
        Log.d(className, "setAlarm method start");
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(AddActivity.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(className, "Alarm Manager is null");
            return;
        }
        Intent alarmIntent = new Intent(AddActivity.this, AlarmReceiver.class);
        alarmIntent.putExtra("ticketDescription", ticketDescription.getText().toString());
        alarmIntent.putExtra("code", "activateAlarm");
        alarmIntent.putExtra("fromStation", fromStation.getText().toString());
        alarmIntent.putExtra("toStation", toStation.getText().toString());
        alarmIntent.putExtra("ticketId", ticketId);

        PendingIntent pendingAlarmIntent =
                PendingIntent.getBroadcast(getApplicationContext(), ticketId, alarmIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, remindTime, pendingAlarmIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, remindTime, pendingAlarmIntent);
        }
        Log.d(className, "setAlarm method end");
    }

    /**
     * Shows date picker or time picker dialog based on the parameter value.
     *
     * @param dialogToShow datePicker or timePicker
     * @return Dialag
     */
    // TODO use int instead of string, if predetermined values, use enum
    private Dialog showDialog(String dialogToShow) {
        Calendar c = Calendar.getInstance();
        Dialog dialog = null;
        switch (dialogToShow) {
            case "datePicker":
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                dialog = new DatePickerDialog(AddActivity.this, date_listener, year, month, day);
                break;

            case "timePicker":
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                dialog = new TimePickerDialog(AddActivity.this, time_listener, hour, minute,
                        DateFormat.is24HourFormat(AddActivity.this));
                break;
        }
        return dialog;
    }

    /**
     * Action to be done on selecting date
     */
    DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // updating journey date
            sDate = day;
            sMonth = month;
            sYear = year;
            SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.US);
            SimpleDateFormat formatB = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
            // calculating booking date
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            String weekDay = format.format(calendar.getTime());
            calendar.add(Calendar.DATE, -120);
            // updating booking date
            bDate = calendar.get(Calendar.DATE);
            bMonth = calendar.get(Calendar.MONTH);
            bYear = calendar.get(Calendar.YEAR);

            materialCardView.setVisibility(View.VISIBLE);
            bookingDateText.setText(formatB.format(calendar.getTime()));
            datePickerButton.setText(String.format(getString(R.string.selectedDateFormat), day, (month + 1), year, weekDay));
        }
    };

    /**
     * Action to be done on selecting time
     */
    TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            sHour = hour;
            sMinute = minute;
            alarmTime = getReminderinMillis(bDate, bMonth, bYear, sHour, sMinute);
            timePickerButton.setText(String.format(getString(R.string.selectedTimeFormat), sHour, sMinute));
        }
    };
}
