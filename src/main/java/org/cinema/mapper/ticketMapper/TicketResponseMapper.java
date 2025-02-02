package org.cinema.mapper.ticketMapper;

import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TicketResponseMapper {
    TicketResponseMapper INSTANCE = Mappers.getMapper(TicketResponseMapper.class);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "filmSession", source = "filmSession")
    @Mapping(target = "movieTitle", source = "filmSession.movie.title")
    TicketResponseDTO toDTO(Ticket ticket);

    Ticket toEntity(TicketResponseDTO createDTO);
}