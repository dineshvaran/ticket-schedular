package varanz.android.application.irctcticketreminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.card.MaterialCardView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import java.util.Random;

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

        if(validateInputs()) {

            if (validateSelectedDate() && validateBookingDate()) {
                int radioItemId = alarmRadioGroup.getCheckedRadioButtonId();
                RadioButton radioItem = findViewById(radioItemId);

                if (radioItem.getText().toString().equals(getString(R.string.alarm_radio_item))) {

                    saveData(getString(R.string.alarm_radio_item));
                    setAlarm(alarmTime);
                    Toast.makeText(this, getString(R.string.toast_success_message), Toast.LENGTH_SHORT).show();
                    finish();

                } else {

                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(byear, bmonth, bdate, 8, 0);

                    Calendar endTime = Calendar.getInstance();
                    endTime.set(byear, bmonth, bdate, 8, 30);

                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())

                            // added instead of selectedDate TODO temorary fix
                            .putExtra(CalendarContract.Events.TITLE, "TicketSchedular -- " + "Booking opens")


                            .putExtra(CalendarContract.Events.DESCRIPTION, "Book ticket from " + fromStation.getText().toString()
                                    + " to " + toStation.getText().toString())
                            .putExtra(CalendarContract.Events.EVENT_LOCATION, "www.irctc.co.in")
                            .putExtra(Intent.EXTRA_EMAIL, "algebra.app.help@gmail.com");

                    startActivity(intent);
                }

            } else {
                Toast.makeText(this, getString(R.string.dateError), Toast.LENGTH_LONG).show();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the empty ticket in Database
     * @param reminderType enum ReminderType
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
        datePickerButton = findViewById(R.id.date_picker);
        ticketDescription = findViewById(R.id.ticket_description);
        fromStation = findViewById(R.id.from_station);
        toStation= findViewById(R.id.to_station);
        timePickerButton = findViewById(R.id.time_picker_button);
        alarmRadioGroup= findViewById(R.id.alarm_type);
        materialCardView=findViewById(R.id.material_card_view);
        bookingDateText=findViewById(R.id.booking_date_text);
    }

    /**
     * Validates the user Input
     * @return boolean
     */
    private boolean validateInputs(){
        if(TextUtils.isEmpty(ticketDescription.getText())){
            ticketDescription.setError(getString(R.string.toast_enter_description));
            return false;
        }else{
            ticketDescription.setError(null);
        }

        if(TextUtils.isEmpty(fromStation.getText())){
            fromStation.setError(getString(R.string.toast_enter_from_station));
            return false;
        }else{
            fromStation.setError(null);
        }

        if(TextUtils.isEmpty(toStation.getText())){
            toStation.setError(getString(R.string.toast_enter_to_station));
            return false;
        }else{
            toStation.setError(null);
        }

        return true;
    }

    /**
     * validates whether selected date is less than the present date
     * @return boolean
     */
    private boolean validateSelectedDate(){
        Calendar today = Calendar.getInstance();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(syear,smonth,sdate);
        return selectedDate.getTime().after(today.getTime());

    }

    /**
     * validates whether booking date is less than the present date
     * @return boolean
     */
    private boolean validateBookingDate(){
        Calendar today = Calendar.getInstance();
        Calendar bookingDate = Calendar.getInstance();
        bookingDate.set(byear,bmonth,bdate);
        return bookingDate.getTime().after(today.getTime());
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
     * @param date month date
     * @param month month
     * @param year year
     * @param hour hour
     * @param minute minute
     * @return long
     */
    public long getReminderinMillis(int date, int month, int year, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hour, minute, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Activates the Media service on the given time
     * @param remindTime when to remind in millis
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
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(AddActivity.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, remindTime,
                    AlarmManager.INTERVAL_DAY, pendingAlarmIntent);
        }
        Toast.makeText(this, "You will be reminded when booking opens.", Toast.LENGTH_LONG).show();
        Log.d(className, "setAlarm method end");
    }

    /**
     * Shows date picker or time picker dialog based on the parameter value.
     * @param dialogToShow datePicker or timePicker
     * @return Dialag
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
            SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.US);
            SimpleDateFormat formatB = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
            // calculating booking date
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            String weekDay=format.format(calendar.getTime());
            calendar.add(Calendar.DATE, -120);
            // updating booking date
            bdate=calendar.get(Calendar.DATE);
            bmonth=calendar.get(Calendar.MONTH);
            byear=calendar.get(Calendar.YEAR);

            materialCardView.setVisibility(View.VISIBLE);
            bookingDateText.setText(formatB.format(calendar.getTime()));
            datePickerButton.setText(String.format(getString(R.string.selectedDateFormat), day, (month+1), year, weekDay));
        }
    };

    /**
     * Action to be done on selecting time
     */
    TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {

            long remindTime = getReminderinMillis(sdate, smonth, syear,hour, minute);
            timePickerButton.setText(String.format(getString(R.string.selectedTimeFormat), hour, minute));
            alarmTime = remindTime;
        }
    };
}
