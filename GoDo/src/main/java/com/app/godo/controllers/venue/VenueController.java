package com.app.godo.controllers.venue;


import com.app.godo.dtos.venue.CreateVenueRequestDto;
import com.app.godo.dtos.venue.UpdateVenueDto;
import com.app.godo.dtos.venue.VenueIndexOverviewDto;
import com.app.godo.dtos.venue.VenueOverviewDto;
import com.app.godo.dtos.venue.VenueSearchCriteriaDto;
import com.app.godo.services.venue.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/venue")
@RequiredArgsConstructor
public class VenueController {
    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<Page<VenueIndexOverviewDto>> filterVenues(
            @RequestParam(value = "filter", defaultValue = "") String filter,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "pdfDescription", required = false) String pdfDescription,
            @RequestParam(value = "reviewCountFrom", required = false) Integer reviewCountFrom,
            @RequestParam(value = "reviewCountTo", required = false) Integer reviewCountTo,
            @RequestParam(value = "ratingPerformanceFrom", required = false) Double ratingPerformanceFrom,
            @RequestParam(value = "ratingPerformanceTo", required = false) Double ratingPerformanceTo,
            @RequestParam(value = "ratingAmbientFrom", required = false) Double ratingAmbientFrom,
            @RequestParam(value = "ratingAmbientTo", required = false) Double ratingAmbientTo,
            @RequestParam(value = "ratingVenueFrom", required = false) Double ratingVenueFrom,
            @RequestParam(value = "ratingVenueTo", required = false) Double ratingVenueTo,
            @RequestParam(value = "ratingOverallImpressionFrom", required = false) Double ratingOverallImpressionFrom,
            @RequestParam(value = "ratingOverallImpressionTo", required = false) Double ratingOverallImpressionTo,
            @RequestParam(value = "operator", defaultValue = "AND") String operator,
            @PageableDefault(size = 8, sort = "name", direction = Sort.Direction.ASC) Pageable venuePage
    ){
        VenueSearchCriteriaDto criteria = VenueSearchCriteriaDto.builder()
                .filter(filter)
                .name(name)
                .description(description)
                .pdfDescription(pdfDescription)
                .reviewCountFrom(reviewCountFrom)
                .reviewCountTo(reviewCountTo)
                .ratingPerformanceFrom(ratingPerformanceFrom)
                .ratingPerformanceTo(ratingPerformanceTo)
                .ratingAmbientFrom(ratingAmbientFrom)
                .ratingAmbientTo(ratingAmbientTo)
                .ratingVenueFrom(ratingVenueFrom)
                .ratingVenueTo(ratingVenueTo)
                .ratingOverallImpressionFrom(ratingOverallImpressionFrom)
                .ratingOverallImpressionTo(ratingOverallImpressionTo)
                .operator(operator)
                .build();

        return ResponseEntity.ok(venueService.filterVenues(criteria, venuePage));
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<VenueIndexOverviewDto> createVenue(
            @RequestPart("venue") String venueJson,
            @RequestPart("image") MultipartFile imageFile,
            @RequestPart(value = "description", required = false) MultipartFile pdfFile) {

        CreateVenueRequestDto createVenueRequest = venueService.convertToCreateVenueRequest(venueJson);
        return ResponseEntity.ok(venueService.createVenue(createVenueRequest, imageFile, pdfFile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueIndexOverviewDto> getVenueById(@PathVariable long id) {
          return ResponseEntity.ok(venueService.findVenueById(id));
    }

    @GetMapping("/{id}/more-like-this")
    public ResponseEntity<Page<VenueIndexOverviewDto>> findMoreLikeThis(
            @PathVariable long id,
            @PageableDefault(size = 8) Pageable venuePage
    ) {
        return ResponseEntity.ok(venueService.findMoreLikeThis(id, venuePage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateVenueDto> updateVenue(@PathVariable long id, @RequestBody UpdateVenueDto updateVenueDto) {
        return ResponseEntity.ok(venueService.updateVenue(id, updateVenueDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top")
    public ResponseEntity<List<VenueOverviewDto>> getTopVenues() {
        return ResponseEntity.ok(venueService.findTopVenues());
    }
}
