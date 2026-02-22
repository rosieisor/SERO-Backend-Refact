package com.werp.sero.security.principal;

import com.werp.sero.security.enums.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final int id;
    private final String email;
    private final Type type;
    private final Integer clientId;
    private final List<String> permissions;

    public CustomUserDetails(final Type type, final int id, final String email, final Integer clientId,
                             final List<String> permissions) {
        this.type = type;
        this.id = id;
        this.email = email;
        this.clientId = clientId;
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public Type getType() {
        return this.type;
    }

    public int getId() {
        return this.id;
    }

    public boolean isEmployee() {
        return this.type == Type.EMPLOYEE;
    }

    public Integer getClientId() {
        return this.clientId;
    }
}