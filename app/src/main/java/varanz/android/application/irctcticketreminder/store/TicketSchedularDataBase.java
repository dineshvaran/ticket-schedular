package varanz.android.application.irctcticketreminder.store;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = TicketSchedularEntity.class, version = 2)
@TypeConverters({DateTypeConverter.class})
public abstract class TicketSchedularDataBase extends RoomDatabase{

    public abstract TicketSchedularDao getTicketSchedularDao();

}
