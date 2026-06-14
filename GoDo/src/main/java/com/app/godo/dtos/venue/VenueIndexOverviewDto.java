package com.app.godo.dtos.venue;

import com.app.godo.enums.VenueType;
import com.app.godo.models.Venue;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VenueIndexOverviewDto {
    private long id;
    private String name;
    private String description;
    private String address;
    private double averageRating;
    private VenueType type;
    private String imagePath;
    private String pdfPath;

    public static VenueIndexOverviewDto fromEntity(Venue venue, String imagePath, String pdfPath) {
        return new VenueIndexOverviewDto(
                venue.getId(),
                venue.getName(),
                venue.getDescription(),
                venue.getAddress(),
                venue.getAverageRating(),
                venue.getType(),
                (imagePath != null && !imagePath.isEmpty()) ? venue.getImage().getPath()  : "https://picsum.photos/800/600",
                pdfPath
        );
    }
}
