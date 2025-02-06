package com.fasthub.backend.oper.auth.mapper;

import com.fasthub.backend.oper.auth.dto.JoinDto;
import com.fasthub.backend.oper.auth.dto.UserDto;
import com.fasthub.backend.oper.auth.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    User userDtoToUserEntity(JoinDto userDto);
    UserDto userEntityToUserDto(User user);
}
