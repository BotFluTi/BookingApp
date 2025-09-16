package project.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.entities.RoomType;
import project.entities.User;
import project.repository.UserRepository;
import project.service.BookingService;
import project.service.RoomService;
import project.service.RoomTypeService;

import java.time.LocalDate;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    private final BookingService bookingService;
    private final UserRepository userRepository;

    public RoomController(RoomService roomService,
                          RoomTypeService roomTypeService,
                          BookingService bookingService,
                          UserRepository userRepository) {
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String roomsPage(Model model) {
        model.addAttribute("currentPage", "/rooms");
        model.addAttribute("roomTypes", roomTypeService.findAllTypes());
        return "rooms/rooms";
    }

    @GetMapping("/{type}")
    public String roomDetails(@PathVariable("type") RoomType.TypeCode type, Model model) {
        int nightlyRate = switch (type) {
            case SINGLE -> 120;
            case DOUBLE -> 180;
            case SUITE  -> 300;
        };

        model.addAttribute("currentPage", "/rooms");
        model.addAttribute("type", type);
        model.addAttribute("typeDto", roomTypeService.findByCode(type));
        model.addAttribute("rooms", roomService.findRoomsByType(type));
        model.addAttribute("nightlyRate", nightlyRate);
        model.addAttribute("currentUri", "/rooms/" + type.name());
        return "rooms/room-details";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{type}/add")
    public String addRoom(@PathVariable("type") RoomType.TypeCode type,
                          @RequestParam String roomNumber,
                          RedirectAttributes ra) {
        roomService.createRoomForType(roomNumber, type);
        ra.addFlashAttribute("successMessage", "Room " + roomNumber + " added.");
        return "redirect:/rooms/" + type;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{type}/{id}/availability")
    public String setAvailability(@PathVariable("type") RoomType.TypeCode type,
                                  @PathVariable Long id,
                                  @RequestParam boolean available,
                                  RedirectAttributes ra) {
        roomService.changeAvailability(id, available);
        ra.addFlashAttribute("successMessage", "Room availability updated.");
        return "redirect:/rooms/" + type;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{type}/{id}/delete")
    public String deleteRoom(@PathVariable("type") RoomType.TypeCode type,
                             @PathVariable Long id,
                             RedirectAttributes ra) {
        roomService.deleteRoom(id);
        ra.addFlashAttribute("successMessage", "Room deleted.");
        return "redirect:/rooms/" + type;
    }

    @PostMapping("/{type}/reserve")
    public String reserveRoom(@PathVariable("type") RoomType.TypeCode type,
                              @RequestParam("roomId") Long roomId,
                              @RequestParam("checkIn")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                              @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                              Authentication auth,
                              RedirectAttributes ra) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login?next=/rooms/" + type.name();
        }

        User current = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found: " + auth.getName()));

        try {
            bookingService.createBooking(roomId, current, checkIn, checkOut);
            ra.addFlashAttribute("successMessage",
                    "Booking confirmed for room " + roomId + " (" + checkIn + " → " + checkOut + ").");
        } catch (IllegalArgumentException | IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/rooms/" + type.name();
    }
}
