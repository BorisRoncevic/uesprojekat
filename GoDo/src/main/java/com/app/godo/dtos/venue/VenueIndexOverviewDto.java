package com.app.godo.dtos.venue;

import com.app.godo.enums.VenueType;
import com.app.godo.models.Venue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueIndexOverviewDto {
    private long id;
    private String name;
    private String description;
    private String address;
    private double averageRating;
    private int reviewCount;
    private Double ratingPerformance;
    private Double ratingAmbient;
    private Double ratingVenue;
    private Double ratingOverallImpression;
    private VenueType type;
    private String imagePath;
    private String pdfPath;
    private String highlight;

    public static VenueIndexOverviewDto fromEntity(Venue venue, String imagePath, String pdfPath) {
        return VenueIndexOverviewDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .address(venue.getAddress())
                .averageRating(venue.getAverageRating())
                .reviewCount(venue.getReviews() != null ? venue.getReviews().size() : 0)
                .type(venue.getType())
                .imagePath((imagePath != null && !imagePath.isEmpty()) ? imagePath : "https://picsum.photos/800/600")
                .pdfPath(pdfPath)
                .build();
    }
}
