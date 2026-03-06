package com.werp.sero.security.userdetails;

import com.werp.sero.security.enums.Type;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetails extends UserDetails {
    int getId();

    Type getType();

    Integer getClientId();
}