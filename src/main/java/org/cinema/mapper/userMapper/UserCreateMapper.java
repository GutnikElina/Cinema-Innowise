package org.cinema.mapper.userMapper;

import org.cinema.dto.userDTO.UserCreateDTO;
import org.cinema.model.Role;
import org.cinema.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserCreateMapper {
    UserCreateMapper INSTANCE = Mappers.getMapper(UserCreateMapper.class);

    @Mapping(target = "role", expression = "java(mapRole(userCreateDTO.getRole()))")
    User toEntity(UserCreateDTO userCreateDTO);

    default Role mapRole(String role) {
        return role != null ? Role.valueOf(role.toUpperCase()) : null;
    }
}
