package org.cinema.mapper.filmSessionMapper;

import org.cinema.dto.filmSessionDTO.FilmSessionCreateDTO;
import org.cinema.model.FilmSession;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilmSessionCreateMapper {
    FilmSessionCreateMapper INSTANCE = Mappers.getMapper(FilmSessionCreateMapper.class);

    FilmSession toEntity(FilmSessionCreateDTO filmSessionCreateDTO);
}
