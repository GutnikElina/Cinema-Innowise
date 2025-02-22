package org.cinema.mapper.userMapper;

import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserUpdateMapper {
    UserUpdateMapper INSTANCE = Mappers.getMapper(UserUpdateMapper.class);

    @Mapping(target = "password", source = "password")
    User toEntity(UserUpdateDTO userUpdateDTO);
}
