package com.fasthub.backend.oper.usr.mapper;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.oper.usr.dto.JoinDto;
import com.fasthub.backend.oper.usr.dto.UserDto;
import com.fasthub.backend.oper.usr.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T13:13:52+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public User userDtoToUserEntity(JoinDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.userId( userDto.getUserId() );
        user.userNm( userDto.getUserNm() );
        user.userAge( userDto.getUserAge() );
        if ( userDto.getAuthName() != null ) {
            user.authName( Enum.valueOf( UserRole.class, userDto.getAuthName() ) );
        }

        return user.build();
    }

    @Override
    public UserDto userEntityToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setId( user.getId() );
        userDto.setUserId( user.getUserId() );
        userDto.setUserNm( user.getUserNm() );
        userDto.setUserAge( user.getUserAge() );
        userDto.setAuthName( user.getAuthName() );

        return userDto;
    }
}
