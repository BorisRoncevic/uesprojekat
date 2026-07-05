package com.app.godo.dtos.event;

import com.app.godo.enums.EventType;
import com.app.godo.models.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailsDto {
    private static final String FALLBACK_IMAGE_URL = "https://picsum.photos/800/600";

    private long id;
    private String name;
    private String description;
    private LocalDate date;
    private EventType type;
    private double price;
    private String imagePath;
    private boolean recurrent;
    private String address;
    private long venueId;


    public static EventDetailsDto fromEntity(Event event) {
        return new EventDetailsDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getDate(),
                event.getEventType(),
                event.getPrice(),
                event.getImage() != null ? event.getImage().getPath() : FALLBACK_IMAGE_URL,
                event.isRecurrent(),
                event.getAddress(),
                event.getVenue().getId()
        );
    }
}
