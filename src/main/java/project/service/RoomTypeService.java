package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.common.RoomTypeDto;
import project.entities.RoomType;
import project.repository.RoomTypeRepository;

import java.util.List;

@Service
public class RoomTypeService {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    public List<RoomTypeDto> findAllTypes() {
        return roomTypeRepository.findAll()
                .stream()
                .map(RoomTypeDto::fromEntity)
                .toList();
    }

    public RoomTypeDto findById(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RoomType with ID " + id + " not found"));
        return RoomTypeDto.fromEntity(roomType);
    }

    public void createRoomType(String typeCode, String name, String description, String imagePath) {
        RoomType roomType = new RoomType();
        roomType.setCode(RoomType.TypeCode.valueOf(typeCode.toUpperCase()));
        roomType.setName(name);
        roomType.setDescription(description);
        roomType.setImagePath(imagePath);
        roomTypeRepository.save(roomType);
    }

    public void updateRoomType(Long id, String name, String description, String imagePath) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RoomType with ID " + id + " not found"));
        if (name != null && !name.trim().isEmpty()) {
            roomType.setName(name);
        }
        if (description != null) {
            roomType.setDescription(description);
        }
        if (imagePath != null) {
            roomType.setImagePath(imagePath);
        }
        roomTypeRepository.save(roomType);
    }

    public void deleteRoomType(Long id) {
        roomTypeRepository.findById(id)
                .ifPresent(roomTypeRepository::delete);
    }

    public RoomTypeDto findByCode(RoomType.TypeCode code) {
        RoomType rt = roomTypeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("RoomType not found for code: " + code));
        return RoomTypeDto.fromEntity(rt);
    }
}
