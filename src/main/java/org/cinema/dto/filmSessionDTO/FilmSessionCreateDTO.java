package org.cinema.dto.filmSessionDTO;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for creating a new film session with required details.
 */
@Data
@Builder
public class FilmSessionCreateDTO {
    private BigDecimal price;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
}
