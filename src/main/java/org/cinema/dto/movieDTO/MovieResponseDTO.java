package org.cinema.dto.movieDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieResponseDTO {
    private Long id;
    private String title;
    private String year;
    private String poster;
    private String genre;
    private String imdbRating;
    private String runtime;
}
