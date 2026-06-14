package com.app.godo.services.venue;

import com.app.godo.dtos.venue.CreateVenueRequestDto;
import com.app.godo.dtos.venue.UpdateVenueDto;
import com.app.godo.dtos.venue.VenueIndexOverviewDto;
import com.app.godo.dtos.venue.VenueOverviewDto;
import com.app.godo.enums.VenueType;
import com.app.godo.exceptions.general.ConflictException;
import com.app.godo.exceptions.general.NotFoundException;
import com.app.godo.exceptions.general.ParseException;
import com.app.godo.models.Image;
import com.app.godo.models.Review;
import com.app.godo.models.Venue;
import com.app.godo.models.VenueDocument;
import com.app.godo.repositories.event.EventRepository;
import com.app.godo.repositories.venue.VenueESRepository;
import com.app.godo.repositories.venue.VenueRepository;
import com.app.godo.services.event.EventService;
import com.app.godo.services.files.FileStorageService;
import com.app.godo.services.files.MinIOService;
import com.app.godo.utils.PDFParserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final VenueESRepository venueElasticsearchRepository;
    private final MinIOService minIOService;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LogManager.getLogger(VenueService.class);

    public Page<VenueIndexOverviewDto> filterVenues(String filter, int venueType, Pageable pageable) {
        Page<Venue> venues;

        if (venueType == -1) {
            venues = venueRepository.filterVenues(filter, filter, pageable);
        }
        else {
            venues = venueRepository.filterVenuesWithType(filter, filter, VenueType.values()[venueType], pageable);
        }

        return venues.map(venue -> VenueIndexOverviewDto.fromEntity(venue,
                minIOService.getFileUrl(venue.getImageFilename()),
                minIOService.getFileUrl(venue.getPdfFilename())));
    }

    @Transactional
    public VenueIndexOverviewDto createVenue(CreateVenueRequestDto venueRequest, MultipartFile venueImage, MultipartFile venuePdf) {
        Venue existingVenue = venueRepository.findVenueByName(venueRequest.getName());

        if (existingVenue != null) { throw new ConflictException("A venue with the entered name already exists!"); }

        String imageName = minIOService.uploadFile(venueImage);
        logger.info("Venue image is saved as {}", imageName);

        String pdfName = minIOService.uploadFile(venuePdf);
        logger.info("Venue PDF is saved as {}", pdfName);

        Venue venue = Venue.builder()
                .name(venueRequest.getName())
                .description(venueRequest.getDescription())
                .address(venueRequest.getAddress())
                .type(venueRequest.getType())
                .averageRating(0)
                .createdAt(LocalDate.from(LocalDateTime.now()))
                .imageFilename(imageName)
                .pdfFilename(pdfName)
                .build();


        var savedVenue = venueRepository.save(venue);

        String extractedPdfText = PDFParserUtil.extractTextFromPDF(venuePdf);

        VenueDocument doc = VenueDocument.builder()
                .id(savedVenue.getId())
                .name(savedVenue.getName())
                .description(savedVenue.getDescription())
                .address(savedVenue.getAddress())
                .type(savedVenue.getType().name())
                .imageFilename(savedVenue.getImageFilename())
                .pdfFilename(savedVenue.getPdfFilename())
                .pdfDescription(extractedPdfText)
                .reviewCount(0)
                .averageRating(0.0)
                .ratingPerformance(0.0)
                .ratingAmbient(0.0)
                .ratingVenue(0.0)
                .ratingOverallImpression(0.0)
                .build();

        try {
            venueElasticsearchRepository.save(doc);
            logger.info("Successfully indexed venue '{}' [ID: {}] in Elasticsearch.", savedVenue.getName(), savedVenue.getId());
        } catch (Exception e) {
            // We log the error but do not crash the transaction.
            // This ensures the database transaction remains safe even if the search cluster hits a transient issue.
            logger.error("Failed to index venue in Elasticsearch: ", e);
        }

        String imagePath = minIOService.getFileUrl(imageName);
        String pdfPath = minIOService.getFileUrl(pdfName);

        return VenueIndexOverviewDto.fromEntity(venue, imageName, pdfName);
    }

    public VenueIndexOverviewDto findVenueById(long venueId) {
        Venue venue = venueRepository.findVenueById(venueId)
                .orElseThrow(() -> new NotFoundException("The venue you were looking for can't be found"));

        String imagePath = minIOService.getFileUrl(venue.getImageFilename());
        String pdfPath = minIOService.getFileUrl(venue.getPdfFilename());
        logger.info("Fetched image and file for venue with ID: {} => imagePath: {}, pdfPath: {}", venueId, imagePath, pdfPath);

        return VenueIndexOverviewDto.fromEntity(venue, imagePath, pdfPath);
    }

    public UpdateVenueDto updateVenue(long venueId, UpdateVenueDto updateVenueDto) {
        Venue venue = venueRepository.findVenueById(venueId)
                .orElseThrow(() -> new NotFoundException("The venue you were looking for can't be found"));

        venue.setName(updateVenueDto.getName());
        venue.setAddress(updateVenueDto.getAddress());
        venue.setDescription(updateVenueDto.getDescription());
        venue.setType(updateVenueDto.getVenueType());

        venueRepository.save(venue);

        return UpdateVenueDto.fromEntity(venue);
    }

    public void deleteVenue(long venueId) {
        Venue venue = venueRepository.findVenueById(venueId)
                .orElseThrow(() -> new NotFoundException("The venue you were looking for can't be found"));


        if (!eventRepository.findByVenue(venue).isEmpty()) {
            throw new ConflictException("Venue can't be delete if events exist");
        }

        venueRepository.delete(venue);
    }

    public CreateVenueRequestDto convertToCreateVenueRequest(String venueJson) {
        CreateVenueRequestDto venue;
        try {
            venue = objectMapper.readValue(venueJson, CreateVenueRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new ParseException("An expected error has occurred please try again in a moment!");
        }

        return venue;
    }

    public List<VenueOverviewDto> findTopVenues() {
        logger.info("Finding top venues");
        return venueRepository.findAllOrderByCalculatedRatingDesc()
                .stream()
                .limit(3)
                .map(VenueOverviewDto::fromEntity)
                .toList();
    }

    @Transactional
    public void syncVenueToElasticsearch(Long venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new IllegalArgumentException("Venue not found: " + venueId));

        String pdfText = "";
        VenueDocument existingDoc = venueElasticsearchRepository.findById(venueId).orElse(null);
        if (existingDoc != null) {
            pdfText = existingDoc.getPdfDescription();
        }

        java.util.List<Review> validReviews = venue.getReviews().stream()
                .filter(review -> review.getRating() != null)
                .toList();

        int reviewCount = validReviews.size();

        double avgPerformance = validReviews.stream()
                .mapToInt(r -> r.getRating().getPerformance())
                .filter(val -> val > 0)
                .average().orElse(0.0);

        double avgAmbient = validReviews.stream()
                .mapToInt(r -> r.getRating().getAmbient())
                .filter(val -> val > 0)
                .average().orElse(0.0);

        double avgVenueRating = validReviews.stream()
                .mapToInt(r -> r.getRating().getVenue())
                .filter(val -> val > 0)
                .average().orElse(0.0);

        double avgOverall = validReviews.stream()
                .mapToInt(r -> r.getRating().getOverallImpression())
                .filter(val -> val > 0)
                .average().orElse(0.0);

        VenueDocument updatedDoc = VenueDocument.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .address(venue.getAddress())
                .type(venue.getType().name())
                .pdfDescription(pdfText)
                .reviewCount(venue.getReviews().size())
                .averageRating(venue.getAverageRating())
                .ratingPerformance(avgPerformance)
                .ratingAmbient(avgAmbient)
                .ratingVenue(avgVenueRating)
                .ratingOverallImpression(avgOverall)
                .build();

        venueElasticsearchRepository.save(updatedDoc);
        logger.info("Successfully recalculated and synced ES document properties for Venue: {}", venue.getName());
    }
}
