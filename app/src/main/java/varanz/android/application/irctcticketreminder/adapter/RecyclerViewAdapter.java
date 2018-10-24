package varanz.android.application.irctcticketreminder.adapter;

import varanz.android.application.irctcticketreminder.DetailActivity;
import varanz.android.application.irctcticketreminder.R;
import varanz.android.application.irctcticketreminder.store.TicketSchedularEntity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
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
        String fBookingDate= "";
        String fJourneyDate= "";
        String emptyDescription= context.getString(R.string.empty_ticket_description);

        if(TextUtils.isEmpty(ticket.getTicetDescription())){
            viewHolder.tripDescription.setText(emptyDescription);
        }else{
            viewHolder.tripDescription.setText(ticket.getTicetDescription());
        }

        if(null!= ticket.getBookingDate()){
            fBookingDate = String.format(
                    context.getString(R.string.booking_open_desc),
                    format.format(ticket.getBookingDate().getTime())
            );
        }

        if(null!= ticket.getJourneyDate()){
            fJourneyDate = String.format(
                    context.getString(R.string.journeyDateDesc),
                    format.format(ticket.getJourneyDate().getTime())
            );
        }

        viewHolder.bookingText.setText(fBookingDate);
        viewHolder.journeyText.setText(fJourneyDate);
        viewHolder.ticketId.setText(String.valueOf(ticket.getTicketId()));
        if(ticket.getReminderType().equals(context.getString(R.string.radio_item_general))){
            viewHolder.reminderType.setImageDrawable(context.getDrawable(R.drawable.ic_general_black_24dp));
        }else if(ticket.getReminderType().equals(context.getString(R.string.radio_item_takkal))){
            viewHolder.reminderType.setImageDrawable(context.getDrawable(R.drawable.ic_takkal_black_24dp));
        }else{
            // TODO need to add custom
        }

        viewHolder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ticketId = v.findViewById(R.id.hidden_ticket_id);
                String uniqueTicketId = ticketId.getText().toString();
                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra("ticketId", Integer.valueOf(uniqueTicketId));
                context.startActivity(detailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View rowView;
        private TextView tripDescription;
        private TextView bookingText;
        private TextView journeyText;
        private ImageView reminderType;
        private TextView ticketId;

        public ViewHolder(View view){
            super(view);
            rowView = view;
            tripDescription = view.findViewById(R.id.ticket_description);
            bookingText = view.findViewById(R.id.booking_date_text);
            journeyText=view.findViewById(R.id.journey_date_text);
            reminderType=view.findViewById(R.id.reminder_type);
            ticketId=view.findViewById(R.id.hidden_ticket_id);
        }
    }
}
