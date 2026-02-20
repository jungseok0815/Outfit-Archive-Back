package com.fasthub.backend.user.usr.mapper;

import com.fasthub.backend.user.usr.dto.JoinDto;
import com.fasthub.backend.user.usr.dto.UserDto;
import com.fasthub.backend.user.usr.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    User userDtoToUserEntity(JoinDto userDto);
    UserDto userEntityToUserDto(User user);
}
