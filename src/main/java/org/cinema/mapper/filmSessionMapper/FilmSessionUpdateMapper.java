package org.cinema.mapper.filmSessionMapper;

import org.cinema.dto.filmSessionDTO.FilmSessionUpdateDTO;
import org.cinema.model.FilmSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilmSessionUpdateMapper {
    FilmSessionUpdateMapper INSTANCE = Mappers.getMapper(FilmSessionUpdateMapper.class);

    FilmSession toEntity(FilmSessionUpdateDTO filmSessionUpdateDTO);
}
