package com.app.godo.controllers.venue;


import com.app.godo.dtos.venue.CreateVenueRequestDto;
import com.app.godo.dtos.venue.UpdateVenueDto;
import com.app.godo.dtos.venue.VenueIndexOverviewDto;
import com.app.godo.dtos.venue.VenueOverviewDto;
import com.app.godo.models.Venue;
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

import static org.springframework.http.ResponseEntity.noContent;

@RestController
@RequestMapping("/api/venue")
@RequiredArgsConstructor
public class VenueController {
    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<Page<VenueIndexOverviewDto>> filterVenues(
            @RequestParam(value = "filter", defaultValue = "") String filter,
            @RequestParam(value = "venueType", defaultValue = "-1") int venueType,
            @PageableDefault(size = 8, sort = "name", direction = Sort.Direction.ASC) Pageable venuePage
    ){
        return ResponseEntity.ok(venueService.filterVenues(filter, venueType, venuePage));
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
