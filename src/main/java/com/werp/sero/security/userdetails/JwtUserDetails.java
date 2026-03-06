package com.werp.sero.security.userdetails;

import com.werp.sero.security.enums.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUserDetails implements CustomUserDetails {
    private final int id;
    private final String email;
    private final Integer clientId;
    private final Type type;
    private final List<String> permissions;

    public JwtUserDetails(final int id, final String email, final Integer clientId, final Type type,
                          final List<String> permissions) {
        this.id = id;
        this.email = email;
        this.clientId = clientId;
        this.type = type;
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

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Integer getClientId() {
        return this.clientId;
    }
}