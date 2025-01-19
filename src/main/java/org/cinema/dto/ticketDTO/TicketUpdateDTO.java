package org.cinema.dto.ticketDTO;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for updating ticket details.
 */
@Data
@Builder
public class TicketUpdateDTO {
    private Long id;
    private Long userId;
    private Long sessionId;
    private String seatNumber;
    private String status;
    private String requestType;
}
