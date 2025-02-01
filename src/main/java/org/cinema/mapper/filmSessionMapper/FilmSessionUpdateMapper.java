package org.cinema.mapper.filmSessionMapper;

import org.cinema.dto.filmSessionDTO.FilmSessionUpdateDTO;
import org.cinema.model.FilmSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface FilmSessionUpdateMapper {
    FilmSessionUpdateMapper INSTANCE = Mappers.getMapper(FilmSessionUpdateMapper.class);

    FilmSessionUpdateDTO toDTO(Map<String, String> params);
    FilmSession toEntity(FilmSessionUpdateDTO filmSessionUpdateDTO);
}
