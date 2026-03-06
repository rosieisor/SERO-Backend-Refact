package com.werp.sero.security.userdetails;

import com.werp.sero.employee.command.domain.aggregate.ClientEmployee;
import com.werp.sero.security.enums.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ClientEmployeeUserDetails implements CustomUserDetails {
    private final ClientEmployee clientEmployee;
    private final List<String> permissions;

    protected ClientEmployeeUserDetails(final ClientEmployee clientEmployee, final List<String> permissions) {
        this.clientEmployee = clientEmployee;
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
        return clientEmployee.getPassword();
    }

    @Override
    public String getUsername() {
        return clientEmployee.getEmail();
    }

    @Override
    public int getId() {
        return this.clientEmployee.getId();
    }

    @Override
    public Type getType() {
        return Type.CLIENT_EMPLOYEE;
    }

    @Override
    public Integer getClientId() {
        return this.clientEmployee.getClient().getId();
    }
}