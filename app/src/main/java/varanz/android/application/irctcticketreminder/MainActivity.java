package varanz.android.application.irctcticketreminder;

import varanz.android.application.irctcticketreminder.adapter.RecyclerViewAdapter;
import varanz.android.application.irctcticketreminder.bean.TicketDetail;

import varanz.android.application.irctcticketreminder.R;
import varanz.android.application.irctcticketreminder.store.TicketSchedularDataBase;
import varanz.android.application.irctcticketreminder.store.TicketSchedularEntity;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * used for CRUD operation
     */
    TicketSchedularDataBase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(getApplicationContext(),
                TicketSchedularDataBase.class, TicketSchedularEntity.class.getSimpleName())
                .allowMainThreadQueries().build();

    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
        List<TicketSchedularEntity> ticketList = database.getTicketSchedularDao().getAllTickets();
        RecyclerViewAdapter recyclerViewAdapter =
                new RecyclerViewAdapter(ticketList, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(ticketList.size()==0){
            ImageView emptyView = findViewById(R.id.empty_message);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            ImageView emptyView = findViewById(R.id.empty_message);
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * inflates(inserts) menu item in the action bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actionbar_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * action to be done on actionbar menu button click
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}
