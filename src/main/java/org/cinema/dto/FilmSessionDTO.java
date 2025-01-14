package org.cinema.dto;

import lombok.*;
import org.cinema.model.Movie;
import org.cinema.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmSessionDTO {
    private Long id;
    @NonNull
    private Movie movie;
    @NonNull
    private BigDecimal price;
    @NonNull
    private LocalDate date;
    @NonNull
    private LocalTime startTime;
    @NonNull
    private LocalTime endTime;
    @NonNull
    private int capacity;

    public static FilmSessionDTO fromStrings(Movie movie, String dateStr, String startTimeStr,
                                             String endTimeStr, String capacityStr, String priceStr) {
        ValidationUtil.validateDate(dateStr);
        ValidationUtil.validatePrice(priceStr);
        ValidationUtil.validateCapacity(capacityStr);
        ValidationUtil.validateTime(startTimeStr, endTimeStr);

        return FilmSessionDTO.builder()
                .movie(movie)
                .date(LocalDate.parse(dateStr))
                .startTime(LocalTime.parse(startTimeStr))
                .endTime(LocalTime.parse(endTimeStr))
                .capacity(Integer.parseInt(capacityStr))
                .price(new BigDecimal(priceStr))
                .build();
    }

    public static FilmSessionDTO fromStringsWithId(String id, Movie movie, String dateStr,
                                                   String startTimeStr, String endTimeStr,
                                                   String capacityStr, String priceStr) {
        FilmSessionDTO dto = fromStrings(movie, dateStr, startTimeStr, endTimeStr, capacityStr, priceStr);
        dto.setId(ValidationUtil.parseLong(id));
        return dto;
    }
}