package org.cinema.mapper;

import org.cinema.dto.FilmSessionDTO;
import org.cinema.model.FilmSession;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilmSessionMapper {

    FilmSessionMapper INSTANCE = Mappers.getMapper(FilmSessionMapper.class);

    FilmSessionDTO toDTO(FilmSession filmSession);
    FilmSession toEntity(FilmSessionDTO dto);
}
