package com.werp.sero.security.userdetails;

import com.werp.sero.employee.command.domain.aggregate.Employee;
import com.werp.sero.employee.command.domain.repository.EmployeeRepository;
import com.werp.sero.employee.command.exception.EmployeeNotFoundException;
import com.werp.sero.permission.command.domain.repository.EmployeePermissionRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class EmployeeUserDetailsServiceImpl implements UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final EmployeePermissionRepository employeePermissionRepository;

    public EmployeeUserDetailsServiceImpl(final EmployeeRepository employeeRepository,
                                             final EmployeePermissionRepository employeePermissionRepository) {
        this.employeeRepository = employeeRepository;
        this.employeePermissionRepository = employeePermissionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Employee employee = employeeRepository.findByEmailAndStatus(username, "ES_ACT")
                .orElseThrow(EmployeeNotFoundException::new);

        final List<String> permissions = employeePermissionRepository.findPermissionCodeByEmployee(employee);

        return new EmployeeUserDetails(employee, permissions);
    }
}