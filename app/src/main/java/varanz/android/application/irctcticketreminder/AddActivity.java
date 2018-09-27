package varanz.android.application.irctcticketreminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import varanz.android.application.irctcticketreminder.adapter.RecyclerViewAdapter;
import varanz.android.application.irctcticketreminder.receiver.AlarmReceiver;
import varanz.android.application.irctcticketreminder.store.TicketSchedularDataBase;
import varanz.android.application.irctcticketreminder.store.TicketSchedularEntity;

public class AddActivity extends AppCompatActivity {

    private final String className = AddActivity.class.getSimpleName();

    /**
     * used to start service (stores date and time in Millis)
     */
    private long alarmTime;

    // selected date, month and year
    private int sdate=0;
    private int smonth=0;
    private int syear=0;

    // booking date, month and year
    private int bdate=0;
    private int bmonth=0;
    private int byear=0;

    /**
     * Views in this activity
     */
    private TextView selectedDate;
    private TextView bookingDate;
    private TextView bookingMonth;
    private TextView bookingYear;
    private TextView selectedTime;
    private Button datePickerButton;
    private EditText ticketDescription;
    private EditText fromStation;
    private EditText toStation;
    private RadioButton alarmItem;
    private RadioButton gcalendarItem;
    private Button reminderActionButton;
    private RadioGroup alarmRadioGroup;
    private TextView selectedTimeText;

    /**
     * used to handle the active ticket
     */
    boolean isRecordSaved = false;

    // value is 0, when no empty record is inserted.
    int ticketId= 0;

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

        if(validateInputs()){

            int radioItemId = alarmRadioGroup.getCheckedRadioButtonId();
            RadioButton radioItem = findViewById(radioItemId);

            if(radioItem.getText().toString().equals(getString(R.string.alarm_radio_item))){

                saveData(getString(R.string.alarm_radio_item));
                setAlarm(alarmTime);
                Toast.makeText(this, getString(R.string.toast_success_message), Toast.LENGTH_SHORT).show();
                finish();

            }else{

                Calendar beginTime = Calendar.getInstance();
                beginTime.set(byear, bmonth, bdate,8, 00);

                Calendar endTime = Calendar.getInstance();
                endTime.set(byear, bmonth, bdate,8, 30);

                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE,
                                "TicketSchedular -- " + "Booking opens today for the date" + selectedDate.getText().toString())
                        .putExtra(CalendarContract.Events.DESCRIPTION, "Book ticket from " + fromStation.getText().toString()
                            + " to " + toStation.getText().toString())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "www.irctc.co.in")
                        .putExtra(Intent.EXTRA_EMAIL, "algebra.app.help@gmail.com");

                startActivity(intent);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the empty ticket in Database
     * @param reminderType
     */
    public void saveData(String reminderType){

        Calendar journey = Calendar.getInstance();
        journey.set(syear,smonth,sdate);

        Calendar booking = Calendar.getInstance();
        booking.set(byear,bmonth,bdate);

        TicketSchedularEntity entity = new TicketSchedularEntity();
        entity.setTicketId(ticketId);
        entity.setTicetDescription(ticketDescription.getText().toString());
        entity.setFromStation(fromStation.getText().toString());
        entity.setToStation(toStation.getText().toString());
        entity.setReminderType(reminderType);
        entity.setJourneyDate(journey);
        entity.setBookingDate(booking);

        database.getTicketSchedularDao().updateTicketDetail(entity);
        isRecordSaved= true;
    }


    /**
     * Inserts an empty record in the database
     */
    public void insertEmptyData(){
        TicketSchedularEntity entity = new TicketSchedularEntity();
        ticketId= (int)database.getTicketSchedularDao().insertTicketDetail(entity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteEmptyData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteEmptyData();
    }

    /**
     * deletes empty data when record is not saved.
     */
    private void deleteEmptyData(){

        if(!isRecordSaved){
            database.getTicketSchedularDao().deleteTicketDetailByTicketId(ticketId);
        }
    }

    /**
     * Initializes the Views in this activity
     */
    private void initializeViewObjects() {
        selectedDate = findViewById(R.id.selected_date_value);
        bookingDate = findViewById(R.id.booking_date);
        bookingMonth = findViewById(R.id.booking_month);
        bookingYear = findViewById(R.id.booking_year);
        datePickerButton = findViewById(R.id.date_picker);
        ticketDescription = findViewById(R.id.ticket_description);
        selectedTime = findViewById(R.id.selected_time);
        fromStation = findViewById(R.id.from_station);
        toStation= findViewById(R.id.to_station);
        alarmItem= findViewById(R.id.alarm_radio_button);
        gcalendarItem= findViewById(R.id.add_to_calendar_radio_button);
        reminderActionButton = findViewById(R.id.reminder_action);
        alarmRadioGroup= findViewById(R.id.alarm_type);
        selectedTimeText= findViewById(R.id.selected_time_text);
    }

    /**
     * Validates the user Input
     * @return
     */
    private boolean validateInputs(){
        Boolean validationSuccess = false;
        if(TextUtils.isEmpty(ticketDescription.getText())){
            ticketDescription.setError(getString(R.string.toast_enter_description));
            validationSuccess = false;
        }else{
            ticketDescription.setError(null);
            validationSuccess=true;
        }

        if(TextUtils.isEmpty(fromStation.getText())){
            fromStation.setError(getString(R.string.toast_enter_from_station));
            validationSuccess = false;
        }else{
            fromStation.setError(null);
            validationSuccess=true;
        }

        if(TextUtils.isEmpty(toStation.getText())){
            toStation.setError(getString(R.string.toast_enter_to_station));
            validationSuccess = false;
        }else{
            toStation.setError(null);
            validationSuccess=true;
        }

        return validationSuccess;
    }

    /**
     * Listens to the event of Views
     */
    private void addListeners(){

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager =
                        (InputMethodManager) AddActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                View view = AddActivity.this.getCurrentFocus();

                if(null == view){
                    view = new View(AddActivity.this);
                }

                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                showDialog("datePicker").show();
            }
        });

        alarmItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderActionButton.setVisibility(View.VISIBLE);
                selectedTime.setVisibility(View.VISIBLE);
                selectedTimeText.setVisibility(View.VISIBLE);
            }
        });

        gcalendarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderActionButton.setVisibility(View.GONE);
                selectedTime.setVisibility(View.GONE);
                selectedTimeText.setVisibility(View.GONE);
                Toast.makeText(AddActivity.this, getString(R.string.toast_google_calender_info), Toast.LENGTH_SHORT).show();
            }
        });

        reminderActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemId = alarmRadioGroup.getCheckedRadioButtonId();
                RadioButton item = findViewById(itemId);
                String selectedDate = AddActivity.this.selectedDate.getText().toString();

                //TODO remove text from condition, add value for radio item and use it in condition statement
                if(item.getText().toString().equals(getString(R.string.alarm_radio_item)) && sdate!=0){
                        showDialog("timePicker").show();
                }else{
                    Toast.makeText(AddActivity.this, getString(R.string.toast_select_journey_date),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Returns the Calendar Object in Milli Seconds
     * @param date
     * @param month
     * @param year
     * @param hour
     * @param minute
     * @return
     */
    public long getReminderinMillis(int date, int month, int year, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        Calendar present = Calendar.getInstance();
        calendar.set(year, month, date, hour, minute, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Activates the Media service on the given time
     * @param remindTime
     */
    public void setAlarm(long remindTime) {
        Log.d(className, "setAlarm method start");
        Intent alarmIntent = new Intent(AddActivity.this, AlarmReceiver.class);
        alarmIntent.putExtra("ticketDescription", ticketDescription.getText().toString());
        alarmIntent.putExtra("code", "activateAlarm");
        alarmIntent.putExtra("fromStation", fromStation.getText().toString());
        alarmIntent.putExtra("toStation", toStation.getText().toString());

        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(getApplicationContext(),
                new Random().nextInt(1000), alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, remindTime,
                alarmManager.INTERVAL_DAY, pendingAlarmIntent);
        Toast.makeText(this, "You will be reminded when booking opens.", Toast.LENGTH_LONG).show();
        Log.d(className, "setAlarm method end");
    }

    /**
     * Shows date picker or time picker dialog based on the parameter value.
     * @param dialogToShow
     * @return
     */
    // TODO use int instead of string, if predetermined values, use enum
    private Dialog showDialog(String dialogToShow) {
        Calendar c = Calendar.getInstance();
        Dialog dialog = null;
        switch (dialogToShow)
        {
            case "datePicker":
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                dialog = new DatePickerDialog(AddActivity.this, date_listener, year, month, day);
                break;

            case "timePicker":
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                dialog =  new TimePickerDialog(AddActivity.this, time_listener, hour, minute,
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
            sdate=day;
            smonth=month;
            syear=year;

            // calculating booking date
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            calendar.add(Calendar.DATE, -120);

            // updating booking date
            bdate=calendar.get(Calendar.DATE);
            bmonth=calendar.get(Calendar.MONTH);
            byear=calendar.get(Calendar.YEAR);

            bookingDate.setText(String.valueOf(bdate));
            bookingMonth.setText(String.valueOf(bmonth + 1));
            bookingYear.setText(String.valueOf(byear));

        }
    };

    /**
     * Action to be done on selecting time
     */
    TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {

            long remindTime = getReminderinMillis(sdate, smonth, syear,hour, minute);
            selectedTime.setText(hour + getString(R.string.date_spliter) + minute);
            alarmTime = remindTime;
        }
    };
}
