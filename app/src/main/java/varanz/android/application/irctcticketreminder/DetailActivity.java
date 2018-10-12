package varanz.android.application.irctcticketreminder;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import varanz.android.application.irctcticketreminder.store.TicketSchedularDataBase;
import varanz.android.application.irctcticketreminder.store.TicketSchedularEntity;

public class DetailActivity extends AppCompatActivity {

    /**
     * used for CRUD operation
     */
    TicketSchedularDataBase database;

    /**
     *
     * ticket Id
     */
    int ticketId=-1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        afterOnCreate();
    }

    private void afterOnCreate(){
        database = Room.databaseBuilder(getApplicationContext(),
                TicketSchedularDataBase.class, TicketSchedularEntity.class.getSimpleName())
                .allowMainThreadQueries().build();
        ticketId=getIntent().getIntExtra("ticketId",-1);
    }

    private void initialize(){
        
    }

    private TicketSchedularEntity getData(int ticketId){
        return database.getTicketSchedularDao().getTicketDetail(ticketId);
    }

    private void fillData(){
        TicketSchedularEntity entity = getData(ticketId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detial_actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
