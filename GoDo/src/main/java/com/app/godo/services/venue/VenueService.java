package com.app.godo.services.venue;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.app.godo.dtos.venue.CreateVenueRequestDto;
import com.app.godo.dtos.venue.UpdateVenueDto;
import com.app.godo.dtos.venue.VenueIndexOverviewDto;
import com.app.godo.dtos.venue.VenueOverviewDto;
import com.app.godo.enums.ReviewStatus;
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
import com.app.godo.services.files.MinIOService;
import com.app.godo.utils.PDFParserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final VenueESRepository venueElasticsearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final MinIOService minIOService;
    private final ObjectMapper objectMapper;

    private static final Map<Character, String> CYRILLIC_TO_LATIN = new HashMap<>();
    static {
        CYRILLIC_TO_LATIN.put('А', "A");  CYRILLIC_TO_LATIN.put('Б', "B");
        CYRILLIC_TO_LATIN.put('В', "V");  CYRILLIC_TO_LATIN.put('Г', "G");
        CYRILLIC_TO_LATIN.put('Д', "D");  CYRILLIC_TO_LATIN.put('Ђ', "Dj");
        CYRILLIC_TO_LATIN.put('Е', "E");  CYRILLIC_TO_LATIN.put('Ж', "Z");
        CYRILLIC_TO_LATIN.put('З', "Z");  CYRILLIC_TO_LATIN.put('И', "I");
        CYRILLIC_TO_LATIN.put('Ј', "J");  CYRILLIC_TO_LATIN.put('К', "K");
        CYRILLIC_TO_LATIN.put('Л', "L");  CYRILLIC_TO_LATIN.put('Љ', "Lj");
        CYRILLIC_TO_LATIN.put('М', "M");  CYRILLIC_TO_LATIN.put('Н', "N");
        CYRILLIC_TO_LATIN.put('Њ', "Nj"); CYRILLIC_TO_LATIN.put('О', "O");
        CYRILLIC_TO_LATIN.put('П', "P");  CYRILLIC_TO_LATIN.put('Р', "R");
        CYRILLIC_TO_LATIN.put('С', "S");  CYRILLIC_TO_LATIN.put('Т', "T");
        CYRILLIC_TO_LATIN.put('Ћ', "C");  CYRILLIC_TO_LATIN.put('У', "U");
        CYRILLIC_TO_LATIN.put('Ф', "F");  CYRILLIC_TO_LATIN.put('Х', "H");
        CYRILLIC_TO_LATIN.put('Ц', "C");  CYRILLIC_TO_LATIN.put('Ч', "C");
        CYRILLIC_TO_LATIN.put('Џ', "Dz"); CYRILLIC_TO_LATIN.put('Ш', "S");

        CYRILLIC_TO_LATIN.put('а', "a");  CYRILLIC_TO_LATIN.put('б', "b");
        CYRILLIC_TO_LATIN.put('в', "v");  CYRILLIC_TO_LATIN.put('г', "g");
        CYRILLIC_TO_LATIN.put('д', "d");  CYRILLIC_TO_LATIN.put('ђ', "dj");
        CYRILLIC_TO_LATIN.put('е', "e");  CYRILLIC_TO_LATIN.put('ж', "z");
        CYRILLIC_TO_LATIN.put('з', "z");  CYRILLIC_TO_LATIN.put('и', "i");
        CYRILLIC_TO_LATIN.put('ј', "j");  CYRILLIC_TO_LATIN.put('к', "k");
        CYRILLIC_TO_LATIN.put('л', "l");  CYRILLIC_TO_LATIN.put('љ', "lj");
        CYRILLIC_TO_LATIN.put('м', "m");  CYRILLIC_TO_LATIN.put('н', "n");
        CYRILLIC_TO_LATIN.put('њ', "nj"); CYRILLIC_TO_LATIN.put('о', "o");
        CYRILLIC_TO_LATIN.put('п', "p");  CYRILLIC_TO_LATIN.put('р', "r");
        CYRILLIC_TO_LATIN.put('с', "s");  CYRILLIC_TO_LATIN.put('т', "t");
        CYRILLIC_TO_LATIN.put('ћ', "c");  CYRILLIC_TO_LATIN.put('у', "u");
        CYRILLIC_TO_LATIN.put('ф', "f");  CYRILLIC_TO_LATIN.put('х', "h");
        CYRILLIC_TO_LATIN.put('ц', "c");  CYRILLIC_TO_LATIN.put('ч', "c");
        CYRILLIC_TO_LATIN.put('џ', "dz"); CYRILLIC_TO_LATIN.put('ш', "s");

        CYRILLIC_TO_LATIN.put('Š', "S");  CYRILLIC_TO_LATIN.put('š', "s");
        CYRILLIC_TO_LATIN.put('Ć', "C");  CYRILLIC_TO_LATIN.put('ć', "c");
        CYRILLIC_TO_LATIN.put('Č', "C");  CYRILLIC_TO_LATIN.put('č', "c");
        CYRILLIC_TO_LATIN.put('Ž', "Z");  CYRILLIC_TO_LATIN.put('ž', "z");
        CYRILLIC_TO_LATIN.put('Đ', "Dj"); CYRILLIC_TO_LATIN.put('đ', "dj");
    }

    private static final Logger logger = LogManager.getLogger(VenueService.class);

    public Page<VenueIndexOverviewDto> filterVenues(String filter, Pageable pageable) {
        BoolQuery.Builder mainBoolQuery = new BoolQuery.Builder();
        boolean hasCriteria = false;

        if (filter != null && !filter.trim().isEmpty()) {
            String trimmed = filter.trim();
            BoolQuery.Builder textShouldQuery = new BoolQuery.Builder();

            if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() > 1) {
                String phrase = trimmed.substring(1, trimmed.length() - 1);
                textShouldQuery.should(QueryBuilders.matchPhrase(m -> m.field("name").query(phrase)));
                textShouldQuery.should(QueryBuilders.matchPhrase(m -> m.field("description").query(phrase)));
                textShouldQuery.should(QueryBuilders.matchPhrase(m -> m.field("pdfDescription").query(phrase)));
            } else if (trimmed.endsWith("*") && trimmed.length() > 1) {
                String prefix = trimmed.substring(0, trimmed.length() - 1);
                String normalizedPrefix = normalizeSerbian(prefix);
                textShouldQuery.should(QueryBuilders.prefix(p -> p.field("name").value(normalizedPrefix)));
                textShouldQuery.should(QueryBuilders.prefix(p -> p.field("description").value(normalizedPrefix)));
                textShouldQuery.should(QueryBuilders.prefix(p -> p.field("pdfDescription").value(normalizedPrefix)));
            } else if (trimmed.startsWith("~") && trimmed.length() > 1) {
                String term = trimmed.substring(1);
                String normalizedTerm = normalizeSerbian(term);
                textShouldQuery.should(QueryBuilders.fuzzy(f -> f.field("name").value(normalizedTerm).fuzziness("AUTO")));
                textShouldQuery.should(QueryBuilders.fuzzy(f -> f.field("description").value(normalizedTerm).fuzziness("AUTO")));
                textShouldQuery.should(QueryBuilders.fuzzy(f -> f.field("pdfDescription").value(normalizedTerm).fuzziness("AUTO")));
            } else {
                textShouldQuery.should(QueryBuilders.match(m -> m.field("name").query(trimmed)));
                textShouldQuery.should(QueryBuilders.match(m -> m.field("description").query(trimmed)));
                textShouldQuery.should(QueryBuilders.match(m -> m.field("pdfDescription").query(trimmed)));
            }

            textShouldQuery.minimumShouldMatch("1");
            mainBoolQuery.must(textShouldQuery.build()._toQuery());
            hasCriteria = true;
        }

        co.elastic.clients.elasticsearch._types.query_dsl.Query finalQuery;
        if (hasCriteria) {
            finalQuery = mainBoolQuery.build()._toQuery();
        } else {
            finalQuery = QueryBuilders.matchAll(m -> m);
        }

        org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder nativeQueryBuilder = NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                String sortField = remapSortField(order.getProperty());

                co.elastic.clients.elasticsearch._types.SortOrder direction =
                        order.isAscending()
                                ? co.elastic.clients.elasticsearch._types.SortOrder.Asc
                                : co.elastic.clients.elasticsearch._types.SortOrder.Desc;

                nativeQueryBuilder.withSort(s -> s.field(f -> f.field(sortField).order(direction)));
            }
        }

        NativeQuery nativeQuery = nativeQueryBuilder.build();

        try {
            SearchHits<VenueDocument> searchHits = elasticsearchOperations.search(nativeQuery, VenueDocument.class);

            List<VenueIndexOverviewDto> mappedDtos = searchHits.getSearchHits().stream()
                    .map(this::mapToDto)
                    .toList();

            return PageableExecutionUtils.getPage(mappedDtos, pageable, searchHits::getTotalHits);

        } catch (org.springframework.data.elasticsearch.UncategorizedElasticsearchException e) {
            Throwable cause = e.getRootCause();
            if (cause instanceof co.elastic.clients.elasticsearch._types.ElasticsearchException esEx) {
                logger.error("=== ELASTICSEARCH SHARD FAILURE DIAGNOSTIC ===");
                logger.error("Error Type: {}", esEx.error().type());
                logger.error("Error Reason: {}", esEx.error().reason());
                if (esEx.error().causedBy() != null) {
                    logger.error("Caused By: {}", esEx.error().causedBy().reason());
                }
                logger.error("=============================================");
            }
            throw e;
        }
    }

    private String remapSortField(String field) {
        return switch (field) {
            case "name" -> "name.keyword";
            default -> field;
        };
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

        venue.setImage(
                Image.builder()
                        .venueImageOf(venue)
                        .path(minIOService.getFileUrl(imageName)).build()
        );

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

    @Transactional
    public UpdateVenueDto updateVenue(long venueId, UpdateVenueDto updateVenueDto) {
        Venue venue = venueRepository.findVenueById(venueId)
                .orElseThrow(() -> new NotFoundException("The venue you were looking for can't be found"));

        venue.setName(updateVenueDto.getName());
        venue.setAddress(updateVenueDto.getAddress());
        venue.setDescription(updateVenueDto.getDescription());
        venue.setType(updateVenueDto.getVenueType());

        venueRepository.save(venue);

        syncVenueToElasticsearch(venue.getId());

        return UpdateVenueDto.fromEntity(venue);
    }

    @Transactional
    public void deleteVenue(long venueId) {
        Venue venue = venueRepository.findVenueById(venueId)
                .orElseThrow(() -> new NotFoundException("The venue you were looking for can't be found"));


        if (!eventRepository.findByVenue(venue).isEmpty()) {
            throw new ConflictException("Venue can't be delete if events exist");
        }

        venueRepository.delete(venue);
        venueElasticsearchRepository.deleteById(venue.getId());
        minIOService.deleteFile(venue.getPdfFilename());
        minIOService.deleteFile(venue.getImageFilename());
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

        List<Review> validReviews = venue.getReviews().stream()
                .filter(review -> review.getStatus() == ReviewStatus.ACTIVE && review.getRating() != null)
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

        double totalSum = validReviews.stream()
                .mapToDouble(r -> r.getRating().getPerformance()
                        + r.getRating().getAmbient()
                        + r.getRating().getVenue()
                        + r.getRating().getOverallImpression())
                .sum();

        double calculatedAverageRating = reviewCount > 0 ? totalSum / (4.0 * reviewCount) : 0.0;

        venue.setAverageRating(calculatedAverageRating);
        venueRepository.save(venue);

        VenueDocument updatedDoc = VenueDocument.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .address(venue.getAddress())
                .type(venue.getType().name())
                .imageFilename(existingDoc.getImageFilename())
                .pdfFilename(existingDoc.getPdfFilename())
                .reviewCount(reviewCount)
                .averageRating(calculatedAverageRating)
                .ratingPerformance(avgPerformance)
                .ratingAmbient(avgAmbient)
                .ratingVenue(avgVenueRating)
                .ratingOverallImpression(avgOverall)
                .build();

        venueElasticsearchRepository.save(updatedDoc);
        logger.info("Successfully recalculated and synced ES document properties for Venue: {}", venue.getName());
    }

    private String normalizeSerbian(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            String replacement = CYRILLIC_TO_LATIN.get(c);
            if (replacement != null) {
                sb.append(replacement);
            } else {
                sb.append(c);
            }
        }
        return sb.toString().toLowerCase();
    }

    private VenueIndexOverviewDto mapToDto(SearchHit<VenueDocument> hit) {
        VenueDocument doc = hit.getContent();

        String descriptionToDisplay = doc.getDescription();

        String imageUrl = minIOService.getFileUrl(doc.getImageFilename());
        String pdfUrl = minIOService.getFileUrl(doc.getPdfFilename());

        return VenueIndexOverviewDto.builder()
                .id(doc.getId())
                .name(doc.getName())
                .description(descriptionToDisplay)
                .type(VenueType.valueOf(doc.getType()))
                .imagePath(imageUrl)
                .pdfPath(pdfUrl)
                .averageRating(doc.getAverageRating())
                .build();
    }
}