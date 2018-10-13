package varanz.android.application.irctcticketreminder;

import varanz.android.application.irctcticketreminder.adapter.RecyclerViewAdapter;

import varanz.android.application.irctcticketreminder.store.TicketSchedularDataBase;
import varanz.android.application.irctcticketreminder.store.TicketSchedularEntity;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * used for CRUD operation
     */
    TicketSchedularDataBase database;

    /**
     * Views
     */
    FloatingActionButton fab;

    /**
     * Migration of database from version 1 to 2
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE TicketSchedularEntity "
                    +"ADD COLUMN reminderHour INTEGER NOT NULL DEFAULT -1");
            database.execSQL("ALTER TABLE TicketSchedularEntity "
                    +"ADD COLUMN reminderMinute INTEGER NOT NULL DEFAULT -1");
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        afterOnCreate();
    }

    /**
     * initializes the values on activity start.
     */
    private void afterOnCreate(){
        initialize();
        addListenerToViews();

        database = Room.databaseBuilder(getApplicationContext(),
                TicketSchedularDataBase.class, TicketSchedularEntity.class.getSimpleName())
                .addMigrations(MIGRATION_1_2)
                .allowMainThreadQueries().build();
    }

    /**
     * initialize's view
     */
    private void initialize(){
        fab=findViewById(R.id.fab);
    }

    /**
     * Adds listeners to views
     */
    private void addListenerToViews(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
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
            TextView emptyView = findViewById(R.id.empty_message);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            TextView emptyView = findViewById(R.id.empty_message);
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

        switch (item.getItemId()){
            case (R.id.action_announcement):
                showAnnouncement();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * shows todays booking announcement
     */
    private void showAnnouncement(){
        Calendar today=Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, 120);
        String formattedDate = new SimpleDateFormat("MMM dd, yyyy").format(today.getTime());
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_announcement_blue_24dp)
                .setTitle("Announcement")
                .setPositiveButton(android.R.string.yes,null)
                .setMessage(String.format("Today booking opens for %1$s", formattedDate))
                .show();
    }
}
