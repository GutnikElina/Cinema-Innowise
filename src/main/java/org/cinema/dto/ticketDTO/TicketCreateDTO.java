package org.cinema.dto.ticketDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketCreateDTO {
    private Long userId;
    private Long sessionId;
    private String seatNumber;
    private String status;
    private String requestType;
}
