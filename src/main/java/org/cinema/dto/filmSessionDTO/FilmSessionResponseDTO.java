package org.cinema.dto.filmSessionDTO;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for responding with film session details, including taken seats.
 */
@Data
@Builder
public class FilmSessionResponseDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private BigDecimal price;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private List<Integer> takenSeats;
}
