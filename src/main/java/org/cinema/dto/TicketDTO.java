package org.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cinema.model.RequestType;
import org.cinema.model.Status;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long id;
    private Long userId;
    private Long sessionId;
    private String seatNumber;
    private LocalDateTime purchaseTime;
    private Status status;
    private RequestType requestType;
}
