package com.fasthub.backend.oper.usr.mapper;

import com.fasthub.backend.oper.usr.dto.JoinDto;
import com.fasthub.backend.oper.usr.dto.UserDto;
import com.fasthub.backend.oper.usr.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    User userDtoToUserEntity(JoinDto userDto);
    UserDto userEntityToUserDto(User user);
}
