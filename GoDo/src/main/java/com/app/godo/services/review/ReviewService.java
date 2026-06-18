package com.app.godo.services.review;

import com.app.godo.dtos.event.EventDetailsDto;
import com.app.godo.dtos.review.CreateReviewDto;
import com.app.godo.dtos.review.RatingOverviewDto;
import com.app.godo.dtos.review.ReviewOverviewDto;
import com.app.godo.enums.ReviewStatus;
import com.app.godo.exceptions.general.ConflictException;
import com.app.godo.exceptions.general.NotFoundException;
import com.app.godo.exceptions.general.UnauthorizedException;
import com.app.godo.models.*;
import com.app.godo.repositories.event.EventRepository;
import com.app.godo.repositories.review.ReviewRepository;
import com.app.godo.repositories.user.UserRepository;
import com.app.godo.repositories.venue.ManagesRepository;
import com.app.godo.repositories.venue.VenueRepository;
import com.app.godo.services.venue.VenueService;
import com.app.godo.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final ManagesRepository managesRepository;
    private final VenueService venueService;

    private final Utils utils;

    private static final Logger logger = LogManager.getLogger(ReviewService.class);

    public Page<ReviewOverviewDto> findReviewPageByVenueId(long venueId, Pageable pageable) {
        logger.info("Finding review page for venue with Id: {}", venueId);

        logger.info("Finding venue with Id: {}", venueId);

        Venue venue;
        try {
            venue = venueRepository.findById(venueId)
                    .orElseThrow(() -> new NotFoundException("The venue you were looking for couldn't be found"));
        } catch (Exception e) {
            logger.warn("Venue with the id: {} couldn't be found", venueId);
            throw new NotFoundException(e.getMessage());
        }

        logger.info("The search for reviews for the venue with Id: {} has been successful", venueId);

        return reviewRepository.findByVenueAndStatus(venue, ReviewStatus.ACTIVE ,pageable)
                .map(review -> ReviewOverviewDto
                .fromEntity(review, eventRepository
                        .findEventNumber(review.getEvent().getId(), review.getEvent().getName()) + 1
                )
        );
    }

    @Transactional
    public void createReview(CreateReviewDto createReviewDto, String token) {
        logger.info("Create review process has begun");

        logger.info("validating request, comparing sender with token subject");

        if (!utils.extractSubject(token).equals(createReviewDto.getReviewer())) {
            logger.warn("Request sender is different than reviewer, review creation failed");
            throw new UnauthorizedException("you are not allowed to perform this action");
        }

        User reviewer;
        Event event;
        Venue venue;

        try {
            logger.info("Finding user with the username: {}", createReviewDto.getReviewer());
            reviewer = userRepository.findByUsername((createReviewDto.getReviewer()))
                    .orElseThrow(() -> new NotFoundException("the user you were looking for couldn't be found"));

            logger.info("User has been found");

        } catch (NotFoundException ex) {
            logger.warn(ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        }

        try {
            logger.info("finding event with the Id: {}", createReviewDto.getEventId());
            event = eventRepository.findById(createReviewDto.getEventId())
                    .orElseThrow(() -> new NotFoundException("the event you were looking for couldn't be found"));

            logger.info("Event has been found");
        } catch (NotFoundException ex) {
            logger.warn(ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        }

        try {
            logger.info("Finding venue with the Id: {}", createReviewDto.getVenueId());
            venue = venueRepository.findById(createReviewDto.getVenueId())
                    .orElseThrow(() -> new NotFoundException("the venue you were looking for couldn't be found"));

            logger.info("Venue has been found");
        } catch (NotFoundException ex) {
            logger.warn(ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        }

        logger.info("initializing comment entity");
        Comment rootComment = Comment.builder()
                .content(createReviewDto.getComment())
                .createdAt(LocalDate.now())
                .commentedBy(reviewer)
                .build();

        logger.info("initializing rating entity");
        Rating rating = Rating.builder()
                .performance(createReviewDto.getPerformanceGrade())
                .ambient(createReviewDto.getAmbientGrade())
                .venue(createReviewDto.getVenueGrade())
                .overallImpression(createReviewDto.getOverallImpression())
                .build();

        logger.info("initializing review entity");
        Review review = Review.builder()
                .status(ReviewStatus.ACTIVE)
                .createdAt(LocalDate.now())
                .reviewedBy(reviewer)
                .event(event)
                .venue(venue)
                .rootComment(rootComment)
                .rating(rating)
                .build();

        rating.setReview(review);

        logger.info("saving review entity");
        Review newReview = reviewRepository.save(review);

        logger.info("review has been created with the Id: {}", newReview.getId());

        venueService.syncVenueToElasticsearch(venue.getId());
    }

    public List<ReviewOverviewDto> findReviewsByVenueId(long venueId) {
        logger.info("Finding reviews for venue with Id: {}", venueId);

        logger.info("Finding venue with Id: {}", venueId);

        Venue venue;
        try {
            venue = venueRepository.findById(venueId)
                    .orElseThrow(() -> new NotFoundException("The venue you were looking for couldn't be found"));
        } catch (Exception e) {
            logger.warn("Venue with the id: {} couldn't be found", venueId);
            throw new NotFoundException(e.getMessage());
        }

        logger.info("Venue with the id : {} has been found, with venue name: {}", venueId, venue.getName());

        logger.info("Looking for reviews for events held at {}", venue.getName());

        List<Review> reviews = reviewRepository.findByVenueAndStatus(venue, ReviewStatus.ACTIVE);

        logger.info("{} reviews for events held at {} have been found", reviews.size(), venue.getName());

        return reviews.stream()
                .map(review -> ReviewOverviewDto
                        .fromEntity(review, eventRepository
                                .findEventNumber(review.getEvent().getId(), review.getEvent().getName())
                        )
                ).toList();
    }

    public RatingOverviewDto findRatingOverviewByVenueId(long venueId) {
        logger.info("Finding rating overview for venue with Id: {}", venueId);

        logger.info("Finding venue with Id: {}", venueId);

        Venue venue;
        try {
            venue = venueRepository.findById(venueId)
                    .orElseThrow(() -> new NotFoundException("The venue you were looking for couldn't be found"));
        } catch (Exception e) {
            logger.warn("Venue with the id: {} couldn't be found", venueId);
            throw new NotFoundException(e.getMessage());
        }

        List<Review> reviews = reviewRepository.findReviewByVenue(venue);

        logger.info("Venue with the id: {} has been found", venueId);

        int excludeReviewCount = 0;
        double ratingSum = 0;
        for (var review : reviews) {
            if (review.getStatus() == ReviewStatus.DELETED) {
                excludeReviewCount++;
                continue;
            }
            logger.info("Calculating rating for review with Id: {}", review.getId());

            ratingSum += review.getRating().getVenue()
                    + review.getRating().getOverallImpression()
                    + review.getRating().getPerformance()
                    + review.getRating().getAmbient();
        }

        logger.info("Average rating for the venue with Id: {} has been calculated", venueId);

        return new RatingOverviewDto(reviews.size() - excludeReviewCount, ratingSum / (4.0 * reviews.size()));
    }

    public void hideReview(long reviewId, String token) {
        String subject = utils.extractSubject(token);
        logger.info("Manager '{}' attempting to hide review with ID: {}", subject, reviewId);

        Review review;
        try {
            review = reviewRepository
                    .findById(reviewId).orElseThrow(() -> new NotFoundException("The review you were looking for couldn't be found!"));
            logger.debug("Fetched review with ID: {}", reviewId);
        } catch (NotFoundException ex) {
            logger.error("Review not found for ID: {}. Message: {}", reviewId, ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        }

        if (review.getStatus().equals(ReviewStatus.HIDDEN)) {
            logger.warn("Review with ID {} is already hidden. Operation aborted.", reviewId);

            throw new ConflictException("Management already hid this review");
        }

        if (managesRepository.findManagement(subject, review.getVenue().getId()).isEmpty()) {
            logger.warn("Unauthorized access attempt by manager '{}' for venue ID {}", subject, review.getVenue().getId());

            throw new UnauthorizedException("You are not allowed to perform this operation!");
        }

        review.setStatus(ReviewStatus.HIDDEN);
        reviewRepository.save(review);

        venueService.syncVenueToElasticsearch(review.getVenue().getId());

        logger.info("Manager '{}' successfully hid a review with the ID: {}", subject, reviewId);
    }

    public void deleteReview(long reviewId, String token) {
        String subject = utils.extractSubject(token);
        logger.info("Manager '{}' attempting to delete review with ID: {}", subject, reviewId);

        Review review;
        try {
            review = reviewRepository
                    .findById(reviewId).orElseThrow(() -> new NotFoundException("The review you were looking for couldn't be found!"));
            logger.debug("Fetched review with ID: {}", reviewId);
        } catch (NotFoundException ex) {
            logger.error("Review not found for ID: {}. Message: {}", reviewId, ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        }

        if (review.getStatus().equals(ReviewStatus.DELETED)) {
            logger.warn("Review with ID {} is already hidden. Operation aborted.", reviewId);

            throw new ConflictException("Management already deleted this review");
        }

        if (managesRepository.findManagement(subject, review.getVenue().getId()).isEmpty()) {
            logger.warn("Unauthorized access attempt by manager '{}' for venue ID {}", subject, review.getVenue().getId());

            throw new UnauthorizedException("You are not allowed to perform this operation!");
        }

        review.setStatus(ReviewStatus.DELETED);
        reviewRepository.save(review);

        venueService.syncVenueToElasticsearch(review.getVenue().getId());

        logger.info("Manager '{}' successfully deleted a review with the ID: {}", subject, reviewId);
    }

    public List<ReviewOverviewDto> findReviewsByUser(String token) {
        String subject = utils.extractSubject(token);

        User user;

        try {
            logger.info("Finding user with the username: {}", subject);
            user = userRepository.findByUsername((subject))
                    .orElseThrow(() -> new NotFoundException("the user you were looking for couldn't be found"));

            logger.info("User has been found");

        } catch (NotFoundException ex) {
            logger.warn(ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        }
        logger.info("Fetching all non-deleted reivews from the user: {}", subject);

        List<Review> reviews = reviewRepository.findByReviewedByIsAndStatusNot(user, ReviewStatus.DELETED);
        logger.info("Fetched {} reviews made by {}", reviews.size(), subject);

        return reviews.stream()
                .map(review -> ReviewOverviewDto
                        .fromEntity(review, eventRepository
                                .findEventNumber(review.getEvent().getId(), review.getEvent().getName())
                        )
                ).toList();
    }
}
