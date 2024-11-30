package org.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Movie {
    private String title;
    private String year;
    private String poster;
    private String plot;
    private String response;
    private String genre;
    private String director;
    private String actors;
    private String imdbRating;
    private String runtime;
}
