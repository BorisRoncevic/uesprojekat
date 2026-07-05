package com.app.godo.utils;

import com.app.godo.enums.EventType;
import com.app.godo.enums.ProfileStatus;
import com.app.godo.enums.ReviewStatus;
import com.app.godo.models.Comment;
import com.app.godo.models.Event;
import com.app.godo.models.Rating;
import com.app.godo.models.Review;
import com.app.godo.models.User;
import com.app.godo.models.Venue;
import com.app.godo.repositories.event.EventRepository;
import com.app.godo.repositories.review.ReviewRepository;
import com.app.godo.repositories.user.UserRepository;
import com.app.godo.repositories.venue.VenueRepository;
import com.app.godo.services.venue.VenueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoReviewSeeder {
    private static final String DEMO_PREFIX = "[UES DEMO]";

    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VenueService venueService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedVenueReviews() {
        List<Venue> venues = venueRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Venue::getName))
                .toList();

        if (venues.isEmpty()) {
            return;
        }

        List<User> reviewers = ensureReviewers();
        for (int i = 0; i < venues.size(); i++) {
            Venue venue = venues.get(i);
            if (hasDemoReviews(venue)) {
                continue;
            }

            Event event = ensureReviewEvent(venue, i);
            int reviewCount = (i % 5) + 1;

            for (int j = 0; j < reviewCount; j++) {
                int[] scores = scoresFor(i, j);
                User reviewer = reviewers.get((i + j) % reviewers.size());

                Comment rootComment = Comment.builder()
                        .content(DEMO_PREFIX + " " + demoComment(venue.getName(), scores))
                        .createdAt(LocalDate.now().minusDays((long) i + j + 1))
                        .commentedBy(reviewer)
                        .build();

                Review review = Review.builder()
                        .status(ReviewStatus.ACTIVE)
                        .createdAt(rootComment.getCreatedAt())
                        .reviewedBy(reviewer)
                        .event(event)
                        .venue(venue)
                        .rootComment(rootComment)
                        .build();

                Rating rating = Rating.builder()
                        .performance(scores[0])
                        .ambient(scores[1])
                        .venue(scores[2])
                        .overallImpression(scores[3])
                        .review(review)
                        .build();

                review.setRating(rating);
                reviewRepository.save(review);
            }
        }

        venueService.syncAllVenuesToElasticsearch();
    }

    private boolean hasDemoReviews(Venue venue) {
        return reviewRepository.findReviewByVenue(venue)
                .stream()
                .map(Review::getRootComment)
                .anyMatch(comment -> comment != null
                        && comment.getContent() != null
                        && comment.getContent().startsWith(DEMO_PREFIX));
    }

    private Event ensureReviewEvent(Venue venue, int index) {
        String eventName = DEMO_PREFIX + " Review event - " + venue.getName();

        return eventRepository.findByVenue(venue)
                .stream()
                .filter(event -> eventName.equals(event.getName()))
                .findFirst()
                .orElseGet(() -> eventRepository.save(Event.builder()
                        .name(eventName)
                        .description("Demo event used for venue review and rating search examples.")
                        .eventType(EventType.CULTURE)
                        .date(LocalDate.now().plusDays(30L + index))
                        .address(venue.getAddress())
                        .price(0.0)
                        .recurrent(false)
                        .venue(venue)
                        .build()));
    }

    private List<User> ensureReviewers() {
        List<User> reviewers = new ArrayList<>();
        reviewers.add(ensureReviewer("ues_reviewer1", "ues_reviewer1@example.com"));
        reviewers.add(ensureReviewer("ues_reviewer2", "ues_reviewer2@example.com"));
        reviewers.add(ensureReviewer("ues_reviewer3", "ues_reviewer3@example.com"));
        return reviewers;
    }

    private User ensureReviewer(String username, String email) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(username)
                        .email(email)
                        .password(passwordEncoder.encode("Test123!"))
                        .memberSince(LocalDate.now())
                        .dateOfBirth(LocalDate.of(2000, 1, 1))
                        .phoneNumber(null)
                        .address("UES demo address")
                        .city("Demo City")
                        .profileStatus(ProfileStatus.COMPLETED)
                        .build()));
    }

    private int[] scoresFor(int venueIndex, int reviewIndex) {
        int base = 2 + ((venueIndex + reviewIndex) % 4);
        return new int[] {
                clamp(base + (venueIndex % 2)),
                clamp(base + (reviewIndex % 2)),
                clamp(base + ((venueIndex + reviewIndex) % 2)),
                clamp(base)
        };
    }

    private int clamp(int value) {
        return Math.max(1, Math.min(5, value));
    }

    private String demoComment(String venueName, int[] scores) {
        double average = (scores[0] + scores[1] + scores[2] + scores[3]) / 4.0;
        if (average >= 4.5) {
            return venueName + " has excellent performance, sound, lighting and space.";
        }
        if (average >= 3.5) {
            return venueName + " is a solid place with balanced atmosphere and service.";
        }
        return venueName + " is useful for testing lower rating ranges in Elasticsearch.";
    }
}
