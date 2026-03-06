package com.werp.sero.employee.query.service;

import com.werp.sero.employee.command.exception.ClientEmployeeNotFoundException;
import com.werp.sero.employee.query.dao.ClientEmployeeMapper;
import com.werp.sero.employee.query.dto.EmployeeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ClientEmployeeQueryServiceImpl implements ClientEmployeeQueryService {
    private final ClientEmployeeMapper clientEmployeeMapper;

    @Override
    public EmployeeResponseDTO findClientEmployeeInfoById(final int id) {
        final EmployeeResponseDTO response = clientEmployeeMapper.findById(id);

        if (response == null) {
            throw new ClientEmployeeNotFoundException();
        }

        return response;
    }
}