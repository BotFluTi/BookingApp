package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.common.RoomDto;
import project.entities.Room;
import project.entities.RoomType;
import project.repository.RoomRepository;
import project.repository.RoomTypeRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;


    public List<RoomDto> findAllRooms() {
        return roomRepository.findAll().stream()
                .map(r -> new RoomDto(
                        r.getId(),
                        r.getRoomNumber(),
                        r.getRoomType().getCode().name(),
                        r.isAvailable()
                ))
                .collect(Collectors.toList());
    }

    public void createRoom(String roomNumber, String typeCode) {
        RoomType roomType = roomTypeRepository.findByCode(RoomType.TypeCode.valueOf(typeCode.toUpperCase()))
                .orElseThrow(() -> new RuntimeException("RoomType not found"));

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setAvailable(true);
        roomRepository.save(room);
    }

    public void updateRoom(Long id, String roomNumber, String typeCode, Boolean available) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room with ID " + id + " not found"));

        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            room.setRoomNumber(roomNumber);
        }

        if (typeCode != null && !typeCode.trim().isEmpty()) {
            RoomType roomType = roomTypeRepository.findByCode(RoomType.TypeCode.valueOf(typeCode.toUpperCase()))
                    .orElseThrow(() -> new RuntimeException("RoomType not found"));
            room.setRoomType(roomType);
        }

        if (available != null) {
            room.setAvailable(available);
        }

        roomRepository.save(room);
    }

    public void deleteRoomsByIds(Collection<Long> ids) {
        ids.forEach(id -> roomRepository.findById(id).ifPresent(roomRepository::delete));
    }

    public void changeAvailability(Long id, boolean available) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setAvailable(available);
        roomRepository.save(room);
    }


    public List<RoomDto> findRoomsByType(RoomType.TypeCode code) {
        RoomType rt = roomTypeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("RoomType not found"));

        return roomRepository.findByRoomType(rt).stream()
                .sorted(Comparator.comparing(Room::getRoomNumber)) // ordonare simplă după număr
                .map(r -> new RoomDto(
                        r.getId(),
                        r.getRoomNumber(),
                        r.getRoomType().getCode().name(),
                        r.isAvailable()
                ))
                .collect(Collectors.toList());
    }

    public void createRoomForType(String roomNumber, RoomType.TypeCode code) {
        RoomType rt = roomTypeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("RoomType not found"));

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomType(rt);
        room.setAvailable(true);
        roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
