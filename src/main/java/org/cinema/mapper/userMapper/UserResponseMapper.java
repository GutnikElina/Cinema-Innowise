package org.cinema.mapper.userMapper;

import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserResponseMapper {
    UserResponseMapper INSTANCE = Mappers.getMapper(UserResponseMapper.class);

    UserResponseDTO toDTO(User user);
}
