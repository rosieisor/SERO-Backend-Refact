package com.werp.sero.security.userdetails;

import com.werp.sero.employee.command.domain.aggregate.Employee;
import com.werp.sero.security.enums.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeUserDetails implements CustomUserDetails {
    private final Employee employee;
    private final List<String> permissions;

    protected EmployeeUserDetails(final Employee employee, final List<String> permissions) {
        this.employee = employee;
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
        return this.employee.getPassword();
    }

    @Override
    public String getUsername() {
        return this.employee.getEmail();
    }

    @Override
    public int getId() {
        return this.employee.getId();
    }

    @Override
    public Type getType() {
        return Type.EMPLOYEE;
    }

    @Override
    public Integer getClientId() {
        return null;
    }
}