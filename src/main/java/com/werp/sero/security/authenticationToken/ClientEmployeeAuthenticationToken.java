package com.werp.sero.security.authenticationToken;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class ClientEmployeeAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public ClientEmployeeAuthenticationToken(final String email, final String password) {
        super(email, password);
    }
}