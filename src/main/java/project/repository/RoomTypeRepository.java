package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.entities.RoomType;

import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    Optional<RoomType> findByCode(RoomType.TypeCode code);
}
