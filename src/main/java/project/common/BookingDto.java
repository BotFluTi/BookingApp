package project.common;

import project.entities.Booking;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDto {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String roomType;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String status;
    private LocalDateTime createdAt;

    public BookingDto(Long id, Long roomId, String roomNumber, String roomType,
                      LocalDate checkIn, LocalDate checkOut, String status, LocalDateTime createdAt) {
        this.id = id; this.roomId = roomId; this.roomNumber = roomNumber; this.roomType = roomType;
        this.checkIn = checkIn; this.checkOut = checkOut; this.status = status; this.createdAt = createdAt;
    }

    public static BookingDto fromEntity(Booking b) {
        return new BookingDto(
                b.getId(),
                b.getRoom().getId(),
                b.getRoom().getRoomNumber(),
                b.getRoom().getRoomType().getCode().name(),
                b.getCheckIn(),
                b.getCheckOut(),
                b.getStatus().name(),
                b.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
