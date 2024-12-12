package org.cinema.mapper;

import org.cinema.dto.TicketDTO;
import org.cinema.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class, FilmSessionMapper.class})
public interface TicketMapper {
    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "filmSession.id", target = "sessionId")
    TicketDTO toDTO(Ticket ticket);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "filmSession", ignore = true)
    Ticket toEntity(TicketDTO ticketDTO);
}
