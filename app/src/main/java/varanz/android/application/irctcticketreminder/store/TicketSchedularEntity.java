package varanz.android.application.irctcticketreminder.store;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;

@Entity
public class TicketSchedularEntity {

    @PrimaryKey(autoGenerate = true)
    private int ticketId;

    private String ticetDescription;

    private Calendar journeyDate;

    private Calendar bookingDate;

    private String reminderType;

    private String fromStation;

    private String toStation;

    private int reminderHour;

    private int reminderMinute;

    // Getters and Setters for the field

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicetDescription() {
        return ticetDescription;
    }

    public void setTicetDescription(String ticetDescription) {
        this.ticetDescription = ticetDescription;
    }

    public Calendar getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(Calendar journeyDate) {
        this.journeyDate = journeyDate;
    }

    public Calendar getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Calendar bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public String getFromStation() {
        return fromStation;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }

    public String getToStation() {
        return toStation;
    }

    public void setToStation(String toStation) {
        this.toStation = toStation;
    }

    public int getReminderHour() {
        return reminderHour;
    }

    public void setReminderHour(int reminderHour) {
        this.reminderHour = reminderHour;
    }

    public int getReminderMinute() {
        return reminderMinute;
    }

    public void setReminderMinute(int reminderMinute) {
        this.reminderMinute = reminderMinute;
    }
}
