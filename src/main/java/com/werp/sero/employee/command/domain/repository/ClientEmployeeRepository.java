package com.werp.sero.employee.command.domain.repository;

import com.werp.sero.employee.command.domain.aggregate.ClientEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientEmployeeRepository extends JpaRepository<ClientEmployee, Integer> {
    Optional<ClientEmployee> findByEmail(final String email);

    @Query("SELECT A FROM ClientEmployee A JOIN FETCH A.client WHERE A.email = :email")
    Optional<ClientEmployee> findByEmailFetchJoin(@Param("email") final String email);
}