package org.cinema.dto.factory;

import org.cinema.dto.FilmSessionDTO;
import org.cinema.model.Movie;
import org.cinema.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class FilmSessionDTOFactory {
    public static FilmSessionDTO create(Movie movie, String dateStr, String startTimeStr,
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

    public static FilmSessionDTO createWithId(String id, Movie movie, String dateStr,
                                              String startTimeStr, String endTimeStr,
                                              String capacityStr, String priceStr) {
        FilmSessionDTO dto = create(movie, dateStr, startTimeStr, endTimeStr, capacityStr, priceStr);
        dto.setId(ValidationUtil.parseLong(id));
        return dto;
    }
}
