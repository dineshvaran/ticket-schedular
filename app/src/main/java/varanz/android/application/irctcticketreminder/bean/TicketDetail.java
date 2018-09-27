package varanz.android.application.irctcticketreminder.bean;

import java.util.Calendar;

public class TicketDetail {

    private String ticketDescription;

    private Calendar dateOfJourney;

    public String getTicketDescription() {
        return ticketDescription;
    }

    public void setTicketDescription(String ticketDescription) {
        this.ticketDescription = ticketDescription;
    }

    public Calendar getDateOfJourney() {
        return dateOfJourney;
    }

    public void setDateOfJourney(Calendar dateOfJourney) {
        this.dateOfJourney = dateOfJourney;
    }
}
