package org.cinema.mapper.filmSessionMapper;

import org.cinema.dto.filmSessionDTO.FilmSessionCreateDTO;
import org.cinema.model.FilmSession;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.Map;

@Mapper
public interface FilmSessionCreateMapper {
    FilmSessionCreateMapper INSTANCE = Mappers.getMapper(FilmSessionCreateMapper.class);

    FilmSessionCreateDTO toDTO(Map<String, String> params);
    FilmSession toEntity(FilmSessionCreateDTO filmSessionCreateDTO);
}
