package project.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.entities.RoomType;
import project.entities.User;
import project.service.BookingService;
import project.service.RoomService;
import project.service.RoomTypeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    private final BookingService bookingService;
    public RoomController(RoomService roomService,
                          RoomTypeService roomTypeService,
                          BookingService bookingService) {
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
        this.bookingService = bookingService;
    }


    @GetMapping
    public String roomsPage(Model model, HttpSession session) {
        boolean isAdmin = false;
        Object logged = session.getAttribute("loggedInUser");
        if (logged instanceof User u) {
            isAdmin = (u.getRole() == User.Role.ADMIN);
        }

        model.addAttribute("currentPage", "/rooms");
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("roomTypes", roomTypeService.findAllTypes());
        return "rooms/rooms";
    }

    @GetMapping("/{type}")
    public String roomDetails(@PathVariable("type") RoomType.TypeCode type,
                              Model model,
                              HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login?next=/rooms/" + type.name();
        }

        boolean isAdmin = false;
        Object logged = session.getAttribute("loggedInUser");
        if (logged instanceof User u) {
            isAdmin = (u.getRole() == User.Role.ADMIN);
        }
        int nightlyRate = switch (type) {
            case SINGLE -> 120;
            case DOUBLE -> 180;
            case SUITE  -> 300;
        };

        model.addAttribute("currentPage", "/rooms");
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("type", type);
        model.addAttribute("typeDto", roomTypeService.findByCode(type));
        model.addAttribute("rooms", roomService.findRoomsByType(type));
        model.addAttribute("nightlyRate", nightlyRate);
        return "rooms/room-details";
    }

    @PostMapping("/{type}/add")
    public String addRoom(@PathVariable("type") RoomType.TypeCode type,
                          @RequestParam String roomNumber,
                          HttpSession session) {
        boolean isAdmin = false;
        Object logged = session.getAttribute("loggedInUser");
        if (logged instanceof User u) {
            isAdmin = (u.getRole() == User.Role.ADMIN);
        }
        if (isAdmin) {
            roomService.createRoomForType(roomNumber, type);
        }
        return "redirect:/rooms/" + type;
    }

    @PostMapping("/{type}/{id}/availability")
    public String setAvailability(@PathVariable("type") RoomType.TypeCode type,
                                  @PathVariable Long id,
                                  @RequestParam boolean available,
                                  HttpSession session) {
        boolean isAdmin = false;
        Object logged = session.getAttribute("loggedInUser");
        if (logged instanceof User u) {
            isAdmin = (u.getRole() == User.Role.ADMIN);
        }
        if (isAdmin) {
            roomService.changeAvailability(id, available);
        }
        return "redirect:/rooms/" + type;
    }

    @PostMapping("/{type}/{id}/delete")
    public String deleteRoom(@PathVariable("type") RoomType.TypeCode type,
                             @PathVariable Long id,
                             HttpSession session) {
        boolean isAdmin = false;
        Object logged = session.getAttribute("loggedInUser");
        if (logged instanceof User u) {
            isAdmin = (u.getRole() == User.Role.ADMIN);
        }
        if (isAdmin) {
            roomService.deleteRoom(id);
        }
        return "redirect:/rooms/" + type;
    }




    @PostMapping("/{type}/reserve")
    public String reserveRoom(@PathVariable("type") RoomType.TypeCode type,
                              @RequestParam("roomId") Long roomId,
                              @RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                              @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                              HttpSession session,
                              RedirectAttributes ra) {

        Object logged = session.getAttribute("loggedInUser");
        if (!(logged instanceof User current)) {
            return "redirect:/login?next=/rooms/" + type.name();
        }

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
