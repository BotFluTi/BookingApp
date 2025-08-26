package project.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.entities.User;
import project.repository.UserRepository;
import project.service.BookingService;

import java.util.List;
import java.util.Map;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/my-bookings")
    public String myBookings(Model model, Authentication auth) {
        User u = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found: " + auth.getName()));
        model.addAttribute("currentPage", "/my-bookings");
        model.addAttribute("bookings", bookingService.getUserBookings(u.getId()));
        return "booking/my-bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
                                Authentication auth,
                                RedirectAttributes ra) {
        User u = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found: " + auth.getName()));
        try {
            bookingService.cancelBooking(id, u);
            ra.addFlashAttribute("successMessage", "Booking cancelled.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/my-bookings";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/bookings")
    public String allBookings(Model model) {
        model.addAttribute("currentPage", "/admin/bookings");
        model.addAttribute("bookings", bookingService.getAllCurrentBookings());
        model.addAttribute("usernames", bookingService.getCurrentBookingUsernames());
        return "booking/admin-bookings";
    }

    @GetMapping("/api/rooms/{roomId}/disabled-dates")
    @ResponseBody
    public List<Map<String,String>> disabledDates(@PathVariable Long roomId) {
        return bookingService.getDisabledRanges(roomId).stream()
                .map(r -> Map.of("start", r.start().toString(), "end", r.end().toString()))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/bookings/{id}/cancel")
    public String adminCancel(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        try {
            User admin = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("User not found: " + auth.getName()));
            bookingService.cancelBooking(id, admin);
            ra.addFlashAttribute("successMessage", "Booking cancelled.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/bookings";
    }
}
