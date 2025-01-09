package com.fasthub.backend.oper.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFIlter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;



}
