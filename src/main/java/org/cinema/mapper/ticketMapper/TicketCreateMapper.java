package org.cinema.mapper.ticketMapper;

import org.cinema.dto.ticketDTO.TicketCreateDTO;
import org.cinema.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TicketCreateMapper {
    TicketCreateMapper INSTANCE = Mappers.getMapper(TicketCreateMapper.class);

    Ticket toEntity(TicketCreateDTO createDTO);
    TicketCreateDTO toDTO(Long userId, Long sessionId, String seatNumber);
}
