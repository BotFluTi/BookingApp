package project.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.entities.User;
import project.service.BookingService;

import java.util.List;
import java.util.Map;

@Controller
public class BookingController {

    private final BookingService bookingService;
    public BookingController(BookingService bookingService) { this.bookingService = bookingService; }

    @GetMapping("/my-bookings")
    public String myBookings(Model model, HttpSession session) {
        Object logged = session.getAttribute("loggedInUser");
        if (!(logged instanceof User u)) return "redirect:/login?next=/my-bookings";

        model.addAttribute("currentPage", "/my-bookings");
        model.addAttribute("bookings", bookingService.getUserBookings(u.getId()));
        return "booking/my-bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes ra) {
        Object logged = session.getAttribute("loggedInUser");
        if (!(logged instanceof User u)) return "redirect:/login?next=/my-bookings";

        try {
            bookingService.cancelBooking(id, u);
            ra.addFlashAttribute("successMessage", "Booking cancelled.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/my-bookings";
    }

    @GetMapping("/admin/bookings")
    public String allBookings(Model model, HttpSession session) {
        Object logged = session.getAttribute("loggedInUser");
        if (!(logged instanceof User u) || u.getRole() != User.Role.ADMIN) {
            return "redirect:/";
        }
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

    @PostMapping("/admin/bookings/{id}/cancel")
    public String adminCancel(@PathVariable Long id,
                              HttpSession session,
                              RedirectAttributes ra) {
        Object logged = session.getAttribute("loggedInUser");
        if (!(logged instanceof User u) || u.getRole() != User.Role.ADMIN) {
            return "redirect:/";
        }
        try {
            bookingService.cancelBooking(id, u); // vede mai jos opțiunea pt. past stays
            ra.addFlashAttribute("successMessage", "Booking cancelled.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

}
