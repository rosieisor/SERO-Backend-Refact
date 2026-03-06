package com.werp.sero.security.provider;

import com.werp.sero.security.authenticationToken.ClientEmployeeAuthenticationToken;
import com.werp.sero.security.userdetails.ClientEmployeeUserDetailsServiceImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ClientEmployeeAuthenticationProvider extends DaoAuthenticationProvider {
    public ClientEmployeeAuthenticationProvider(final ClientEmployeeUserDetailsServiceImpl userDetailsService,
                                                final PasswordEncoder passwordEncoder) {
        super(userDetailsService);
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ClientEmployeeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}