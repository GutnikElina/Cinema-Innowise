package org.cinema.model;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String test_year;

    private String poster;

    private String plot;

    private String genre;

    private String imdbRating;

    private String runtime;
}
