package project.common;

import project.entities.RoomType;

public class RoomTypeDto {
    private Long id;
    private String typeCode;
    private String name;
    private String description;
    private String imageUrl;

    public RoomTypeDto(Long id, String typeCode, String name, String description, String imageUrl) {
        this.id = id;
        this.typeCode = typeCode;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static RoomTypeDto fromEntity(RoomType entity) {
        return new RoomTypeDto(
                entity.getId(),
                entity.getCode().name(),
                entity.getName(),
                entity.getDescription(),
                entity.getImagePath()
        );
    }


    public Long getId() {
        return id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
