package org.cinema.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.cinema.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmSessionDTO {
    private int id;
    private String movieTitle;
    private BigDecimal price;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;

    public static FilmSessionDTO fromStrings(String movieTitle, String dateStr, String startTimeStr, 
                                           String endTimeStr, String capacityStr, String priceStr) {
        ValidationUtil.validateDate(dateStr);
        ValidationUtil.validatePrice(priceStr);
        ValidationUtil.validateCapacity(capacityStr);
        ValidationUtil.validateTime(startTimeStr, endTimeStr);

        return FilmSessionDTO.builder()
                .movieTitle(movieTitle)
                .date(LocalDate.parse(dateStr))
                .startTime(LocalTime.parse(startTimeStr))
                .endTime(LocalTime.parse(endTimeStr))
                .capacity(Integer.parseInt(capacityStr))
                .price(new BigDecimal(priceStr))
                .build();
    }

    public static FilmSessionDTO fromStringsWithId(String id, String movieTitle, String dateStr, 
                                                 String startTimeStr, String endTimeStr, 
                                                 String capacityStr, String priceStr) {
        FilmSessionDTO dto = fromStrings(movieTitle, dateStr, startTimeStr, endTimeStr, capacityStr, priceStr);
        dto.setId(Integer.parseInt(id));
        return dto;
    }
}
