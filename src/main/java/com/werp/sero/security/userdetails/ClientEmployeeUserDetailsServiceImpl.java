package com.werp.sero.security.userdetails;

import com.werp.sero.employee.command.domain.aggregate.ClientEmployee;
import com.werp.sero.employee.command.domain.repository.ClientEmployeeRepository;
import com.werp.sero.employee.command.exception.ClientEmployeeNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class ClientEmployeeUserDetailsServiceImpl implements UserDetailsService {
    private final ClientEmployeeRepository clientEmployeeRepository;

    public ClientEmployeeUserDetailsServiceImpl(final ClientEmployeeRepository clientEmployeeRepository) {
        this.clientEmployeeRepository = clientEmployeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final ClientEmployee clientEmployee = clientEmployeeRepository.findByEmailFetchJoin(username)
                .orElseThrow(ClientEmployeeNotFoundException::new);

        return new ClientEmployeeUserDetails(clientEmployee, List.of("AC_CLI"));
    }
}