package org.cinema.dto.filmSessionDTO;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for updating an existing film session with new details.
 */
@Data
@Builder
public class FilmSessionUpdateDTO {
    private Long id;
    private BigDecimal price;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
}
