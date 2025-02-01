package org.cinema.mapper.userMapper;

import org.cinema.dto.userDTO.UserCreateDTO;
import org.cinema.model.Role;
import org.cinema.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserCreateMapper {
    UserCreateMapper INSTANCE = Mappers.getMapper(UserCreateMapper.class);

    @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
    User toEntity(UserCreateDTO userCreateDTO);

    @Named("mapRole")
    default Role mapRole(String role) {
        if (role == null) {
            return null;
        }

        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role, e);
        }
    }
}
