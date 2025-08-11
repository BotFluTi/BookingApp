package project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.common.RoomTypeDto;
import project.entities.RoomType;
import project.service.RoomTypeService;

@Controller
@RequestMapping("/admin/roomtypes")
public class AdminRoomTypeController {

    private final RoomTypeService roomTypeService;

    public AdminRoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    // CREATE – formular
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("mode", "create");
        model.addAttribute("typeCodes", RoomType.TypeCode.values());
        return "admin/roomtype-form";
    }

    // CREATE – submit
    @PostMapping
    public String create(@RequestParam String typeCode,
                         @RequestParam String name,
                         @RequestParam String description,
                         @RequestParam String imagePath) {
        roomTypeService.createRoomType(typeCode, name, description, imagePath);
        return "redirect:/rooms";
    }

    // EDIT – formular
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        RoomTypeDto rt = roomTypeService.findById(id);
        model.addAttribute("mode", "edit");
        model.addAttribute("rt", rt);
        model.addAttribute("typeCodes", RoomType.TypeCode.values());
        return "admin/roomtype-form";
    }

    // EDIT – submit
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String description,
                         @RequestParam String imagePath) {
        roomTypeService.updateRoomType(id, name, description, imagePath);
        return "redirect:/rooms";
    }

    // DELETE
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return "redirect:/rooms";
    }
}
