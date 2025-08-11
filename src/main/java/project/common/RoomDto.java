package project.common;

import project.entities.Room;
import java.util.List;
import java.util.stream.Collectors;

public class RoomDto {
    private Long id;
    private String roomNumber;
    private String roomType;
    private boolean available;

    public RoomDto(Long id, String roomNumber, String roomType, boolean available) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.available = available;
    }

    public Long getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public boolean isAvailable() { return available; }

    public static List<RoomDto> copyRoomsToDto(List<Room> roomList) {
        return roomList.stream()
                .map(room -> new RoomDto(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getRoomType().getCode().name(),
                        room.isAvailable()
                ))
                .collect(Collectors.toList());
    }
}
