package com.werp.sero.employee.query.service;

import com.werp.sero.employee.query.dto.EmployeeResponseDTO;

public interface ClientEmployeeQueryService {
    EmployeeResponseDTO findClientEmployeeInfoById(final int id);
}