package com.app.godo.models;

import com.app.godo.dtos.venue.VenueOverviewDto;
import com.app.godo.enums.VenueType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "venues")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column
    private String imageFilename;

    @Column
    private String pdfFilename;

    private double averageRating;

    @Transient
    private String imagePath;

    @Transient
    private String pdfPath;

    @Column(nullable = false)
    private VenueType type;

    @Column(nullable = false)
    private LocalDate createdAt;

    @OneToOne(mappedBy = "venueImageOf", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Image image;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venue", orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venue", orphanRemoval = true)
    private List<Manages> manages;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venue", orphanRemoval = true)
    private List<Event> events;
}
