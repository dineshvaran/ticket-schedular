package varanz.android.application.irctcticketreminder.store;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Dao
public interface TicketSchedularDao {

    @Insert
    long insertTicketDetail(TicketSchedularEntity entity);

    @Update
    void updateTicketDetail(TicketSchedularEntity entity);

    @Delete
    void deleteTicketDetail(TicketSchedularEntity entity);

    @Query("delete from TicketSchedularEntity where ticketId= :ticketId")
    void deleteTicketDetailByTicketId(int ticketId);

    @Query("select * from TicketSchedularEntity where ticketId= :ticketId")
    TicketSchedularEntity getTicketDetail(int ticketId);

    @Query("select * from TicketSchedularEntity")
    List<TicketSchedularEntity> getAllTickets();
}
