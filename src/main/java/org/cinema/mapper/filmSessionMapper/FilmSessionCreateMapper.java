package org.cinema.mapper.filmSessionMapper;

import org.cinema.dto.filmSessionDTO.FilmSessionCreateDTO;
import org.cinema.model.FilmSession;
import org.cinema.model.Movie;
import org.cinema.repository.impl.MovieRepositoryImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilmSessionCreateMapper {
    FilmSessionCreateMapper INSTANCE = Mappers.getMapper(FilmSessionCreateMapper.class);

    FilmSession toEntity(FilmSessionCreateDTO filmSessionCreateDTO);
}
