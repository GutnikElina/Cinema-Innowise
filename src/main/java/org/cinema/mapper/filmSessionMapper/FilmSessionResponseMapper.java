package org.cinema.mapper.filmSessionMapper;

import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.model.FilmSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilmSessionResponseMapper {
    FilmSessionResponseMapper INSTANCE = Mappers.getMapper(FilmSessionResponseMapper.class);

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "movieTitle", source = "movie.title")
    FilmSessionResponseDTO toDTO(FilmSession filmSession);
}
