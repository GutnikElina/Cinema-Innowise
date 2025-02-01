package org.cinema.mapper.movieMapper;

import org.cinema.model.Movie;
import org.cinema.model.MovieAPI;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MovieApiMapper {
    MovieApiMapper INSTANCE = Mappers.getMapper(MovieApiMapper.class);

    Movie toEntity(MovieAPI movieAPI);
}
