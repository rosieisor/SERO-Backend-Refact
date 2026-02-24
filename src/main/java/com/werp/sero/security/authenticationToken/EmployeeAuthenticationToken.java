package com.werp.sero.security.authenticationToken;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class EmployeeAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public EmployeeAuthenticationToken(final String email, final String password) {
        super(email, password);
    }
}