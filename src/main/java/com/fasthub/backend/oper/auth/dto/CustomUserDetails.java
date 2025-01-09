package com.fasthub.backend.oper.auth.dto;

import com.fasthub.backend.oper.auth.dto.CustomUserInfoDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private final CustomUserInfoDto userInfoDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(userInfoDto.getRole()));
    }

    @Override
    public String getPassword() {
        return userInfoDto.getUserPwd();
    }

    @Override
    public String getUsername() {
        return userInfoDto.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
