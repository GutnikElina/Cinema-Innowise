package org.cinema.mapper;

import org.cinema.dto.MovieDTO;
import org.cinema.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MovieMapper {
    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    MovieDTO toDTO(Movie movie);
    Movie toEntity(MovieDTO movieDTO);
}
