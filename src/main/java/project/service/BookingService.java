package project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.common.BookingDto;
import project.entities.Booking;
import project.entities.Room;
import project.entities.User;
import project.events.BookingEvent;
import project.messaging.BookingEventProducer;
import project.repository.BookingRepository;
import project.repository.RoomRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BookingEventProducer eventProducer;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          BookingEventProducer eventProducer) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.eventProducer = eventProducer;
    }

    public boolean isRoomFree(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepository
                .findOverlapping(roomId, checkIn, checkOut, Booking.Status.CONFIRMED)
                .isEmpty();
    }

    @Transactional
    public BookingDto createBooking(Long roomId, User user, LocalDate checkIn, LocalDate checkOut) {
        if (user == null) throw new IllegalStateException("Login required.");
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Invalid dates (check-in must be before check-out).");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.isAvailable()) {
            throw new IllegalStateException("Room is unavailable (maintenance).");
        }
        if (!isRoomFree(roomId, checkIn, checkOut)) {
            throw new IllegalStateException("Room is already booked for these dates.");
        }

        Booking b = new Booking();
        b.setRoom(room);
        b.setUser(user);
        b.setCheckIn(checkIn);
        b.setCheckOut(checkOut);
        b.setStatus(Booking.Status.CONFIRMED);

        b = bookingRepository.save(b);

        eventProducer.publish(new BookingEvent(
                BookingEvent.Type.CREATED,
                b.getId(),
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                room.getId(),
                b.getCheckIn(),
                b.getCheckOut()
        ));

        return BookingDto.fromEntity(b);
    }

    public List<BookingDto> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(BookingDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelBooking(Long bookingId, User current) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        boolean isOwner = current != null && b.getUser() != null
                && b.getUser().getId().equals(current.getId());
        boolean isAdmin = current != null && current.getRole() == User.Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new IllegalStateException("You cannot cancel this booking.");
        }

        b.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(b);

        User u = b.getUser();
        eventProducer.publish(new BookingEvent(
                BookingEvent.Type.CANCELLED,
                b.getId(),
                u != null ? u.getId() : null,
                u != null ? u.getEmail() : null,
                u != null ? u.getUsername() : null,
                b.getRoom() != null ? b.getRoom().getId() : null,
                b.getCheckIn(),
                b.getCheckOut()
        ));
    }

    public List<BookingDto> getAllCurrentBookings() {
        return bookingRepository
                .findByStatusAndCheckOutAfterOrderByCheckInAsc(Booking.Status.CONFIRMED, LocalDate.now())
                .stream()
                .map(BookingDto::fromEntity)
                .toList();
    }

    public record DateRange(LocalDate start, LocalDate end) {}
    public List<DateRange> getDisabledRanges(Long roomId) {
        return bookingRepository
                .findActiveForRoom(roomId, Booking.Status.CONFIRMED, LocalDate.now())
                .stream()
                .map(b -> new DateRange(b.getCheckIn(), b.getCheckOut()))
                .toList();
    }

    public Map<Long, String> getCurrentBookingUsernames() {
        return bookingRepository.findCurrentUsernames(Booking.Status.CONFIRMED, java.time.LocalDate.now())
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        BookingRepository.IdUsername::getId,
                        BookingRepository.IdUsername::getUsername
                ));
    }
}
