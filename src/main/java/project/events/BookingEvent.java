package project.events;

import java.time.LocalDate;

public class BookingEvent {
    public enum Type { CREATED, CANCELLED }

    private Type type;
    private Long bookingId;
    private Long userId;
    private String userEmail;
    private String username;
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;

    public BookingEvent() {}

    public BookingEvent(Type type, Long bookingId, Long userId, String userEmail, String username,
                        Long roomId, LocalDate checkIn, LocalDate checkOut) {
        this.type = type;
        this.bookingId = bookingId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.username = username;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
}
