package com.werp.sero.employee.query.dao;

import com.werp.sero.employee.query.dto.EmployeeResponseDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientEmployeeMapper {
    EmployeeResponseDTO findById(final int id);
}