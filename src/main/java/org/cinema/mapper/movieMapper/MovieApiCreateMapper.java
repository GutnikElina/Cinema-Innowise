package org.cinema.mapper.movieMapper;

import org.cinema.model.Movie;
import org.cinema.model.MovieAPI;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MovieApiCreateMapper {
    MovieApiCreateMapper INSTANCE = Mappers.getMapper(MovieApiCreateMapper.class);

    Movie toEntity(MovieAPI movieAPI);
}
