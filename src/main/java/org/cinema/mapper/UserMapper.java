package org.cinema.mapper;

import org.cinema.dto.UserDTO;
import org.cinema.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(imports = LocalDateTime.class)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", expression = "java(Long.valueOf(user.getId()))")
    UserDTO toDTO(User user);
    
    @Mapping(target = "id", expression = "java((int) userDTO.getId().longValue())")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    User toEntity(UserDTO userDTO);
}
