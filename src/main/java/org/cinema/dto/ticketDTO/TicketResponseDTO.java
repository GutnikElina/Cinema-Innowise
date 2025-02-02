package org.cinema.dto.ticketDTO;

import lombok.Builder;
import lombok.Data;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private FilmSessionResponseDTO filmSession;
    private String movieTitle;
    private String seatNumber;
    private LocalDateTime purchaseTime;
    private String status;
    private String requestType;
}
