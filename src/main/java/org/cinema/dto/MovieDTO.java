package org.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Long id;
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
