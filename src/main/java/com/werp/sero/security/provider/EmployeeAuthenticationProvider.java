package com.werp.sero.security.provider;

import com.werp.sero.security.authenticationToken.EmployeeAuthenticationToken;
import com.werp.sero.security.userdetails.EmployeeUserDetailsServiceImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EmployeeAuthenticationProvider extends DaoAuthenticationProvider {
    public EmployeeAuthenticationProvider(final EmployeeUserDetailsServiceImpl userDetailsService,
                                          final PasswordEncoder passwordEncoder) {
        super(userDetailsService);
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmployeeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}