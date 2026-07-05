package com.app.godo.dtos.venue;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VenueSearchCriteriaDto {
    private String filter;
    private String name;
    private String description;
    private String pdfDescription;
    private Integer reviewCountFrom;
    private Integer reviewCountTo;
    private Double ratingPerformanceFrom;
    private Double ratingPerformanceTo;
    private Double ratingAmbientFrom;
    private Double ratingAmbientTo;
    private Double ratingVenueFrom;
    private Double ratingVenueTo;
    private Double ratingOverallImpressionFrom;
    private Double ratingOverallImpressionTo;
    private String operator;
}
