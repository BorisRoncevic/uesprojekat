package com.app.godo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = false, nullable = true)
    private String path = "https://picsum.photos/800/600";

    @Column(unique = false, nullable = true)
    private String name;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User profileImageOf;

    @OneToOne
    @JoinColumn(name = "venue_id", referencedColumnName = "id")
    @JsonBackReference
    private Venue venueImageOf;

    @OneToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @JsonBackReference
    private Event eventImageOf;
}
