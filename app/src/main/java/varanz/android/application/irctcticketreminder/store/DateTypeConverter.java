package varanz.android.application.irctcticketreminder.store;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;

public class DateTypeConverter {

    @TypeConverter
    public Long calendarToMillis(Calendar calendar){
        if(null== calendar){
            return null;
        }

        return calendar.getTimeInMillis();
    }

    @TypeConverter
    public Calendar millisToCalendar(Long millis){
        if(null== millis){
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }
}
