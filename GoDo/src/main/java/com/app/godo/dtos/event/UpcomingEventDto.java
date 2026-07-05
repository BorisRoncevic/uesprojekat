package com.app.godo.dtos.event;

import com.app.godo.enums.EventType;
import com.app.godo.models.Event;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingEventDto {
    private static final String FALLBACK_IMAGE_URL = "https://picsum.photos/800/600";

    private long id;
    private String name;
    private double price;
    private EventType eventType;
    private String imagePath;
    private LocalDate date;

    public static UpcomingEventDto fromEntity(Event event) {
        return new UpcomingEventDto(
                event.getId(),
                event.getName(),
                event.getPrice(),
                event.getEventType(),
                event.getImage() != null ? event.getImage().getPath() : FALLBACK_IMAGE_URL,
                event.getDate()
        );
    }
}
