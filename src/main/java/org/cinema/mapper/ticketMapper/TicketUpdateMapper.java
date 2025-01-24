package org.cinema.mapper.ticketMapper;

import org.cinema.dto.ticketDTO.TicketUpdateDTO;
import org.cinema.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TicketUpdateMapper {
    TicketUpdateMapper INSTANCE = Mappers.getMapper(TicketUpdateMapper.class);

    Ticket toEntity(TicketUpdateDTO updateDTO);
}
