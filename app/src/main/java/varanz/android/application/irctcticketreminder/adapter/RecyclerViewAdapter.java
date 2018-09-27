package varanz.android.application.irctcticketreminder.adapter;

import varanz.android.application.irctcticketreminder.R;
import varanz.android.application.irctcticketreminder.bean.TicketDetail;
import varanz.android.application.irctcticketreminder.store.TicketSchedularEntity;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final List<TicketSchedularEntity> ticketList;
    private final Context context;

    public RecyclerViewAdapter(List<TicketSchedularEntity> ticketList, Context context){
        this.ticketList = ticketList;
        this.context= context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.row_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
        TicketSchedularEntity ticket = ticketList.get(index);
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        String journeyDateString= "";
        String emptyDescription= context.getString(R.string.empty_ticket_description);
        Date bookingDate = null;
        if(null!= ticket.getJourneyDate()){
            bookingDate = ticket.getBookingDate().getTime();
            journeyDateString = String.format(
                    context.getString(R.string.booking_open_desc),
                    format.format(bookingDate)
            );
        }

        if(TextUtils.isEmpty(ticket.getTicetDescription())){
            viewHolder.tripDescription.setText(emptyDescription);
        }else{
            viewHolder.tripDescription.setText(ticket.getTicetDescription());
        }

        viewHolder.bookingText.setText(journeyDateString);

//        viewHolder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView tripDescription;
        private TextView bookingText;

        public ViewHolder(View view){
            super(view);
            view = view;
            tripDescription = view.findViewById(R.id.ticket_description);
            bookingText = view.findViewById(R.id.booking_date_text);
        }
    }
}
