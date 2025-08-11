package project.repository;// RoomRepository.java
import org.springframework.data.jpa.repository.JpaRepository;
import project.entities.Room;
import project.entities.RoomType;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByRoomType(RoomType roomType);

    List<Room> findByRoomTypeOrderByRoomNumberAsc(RoomType roomType);

    boolean existsByRoomNumber(String roomNumber);
}
