package org.cinema.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String year;
    private String poster;
    private String plot;
    private String genre;
    private String director;
    private String actors;
    private String imdbRating;
    private String runtime;
}
