package org.cinema.dto;

import lombok.*;
import org.cinema.model.Movie;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilmSessionDTO {
    private Long id;
    private Movie movie;
    private BigDecimal price;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
}