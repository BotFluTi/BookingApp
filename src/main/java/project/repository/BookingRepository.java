
package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.entities.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
           select b from Booking b
           where b.room.id = :roomId
             and b.status = :status
             and (b.checkIn < :end and b.checkOut > :start)
           """)
    List<Booking> findOverlapping(Long roomId, LocalDate start, LocalDate end, Booking.Status status);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    List<Booking> findByStatusAndCheckOutAfterOrderByCheckInAsc(Booking.Status status, LocalDate date);

    @Query("""
           select b from Booking b
           where b.room.id = :roomId
             and b.status = :status
             and b.checkOut > :from
           order by b.checkIn asc
           """)
    List<Booking> findActiveForRoom(Long roomId, Booking.Status status, LocalDate from);

    interface IdUsername {
        Long getId();
        String getUsername();
    }

    @Query("""
       select b.id as id, u.username as username
       from Booking b
       join b.user u
       where b.status = :status
         and b.checkOut > :date
       order by b.checkIn asc
    """)
    List<IdUsername> findCurrentUsernames(Booking.Status status, java.time.LocalDate date);

}
