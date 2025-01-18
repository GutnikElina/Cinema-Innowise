package org.cinema.mapper.movieMapper;

import org.cinema.dto.movieDTO.MovieResponseDTO;
import org.cinema.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MovieResponseMapper {
    MovieResponseMapper INSTANCE = Mappers.getMapper(MovieResponseMapper.class);

    MovieResponseDTO toDTO(Movie movie);
}
