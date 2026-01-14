package com.dudev.orderservice.mapper;

import com.dudev.orderservice.dto.CreateUserDto;
import com.dudev.orderservice.dto.UserDto;
import com.dudev.orderservice.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(CreateUserDto dto);

    List<UserDto> toDtoList(List<User> users);
}
