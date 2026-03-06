package com.werp.sero.employee.query.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {
    private int id;
    private String name;
    private String email;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String positionCode;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String positionName;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String rankCode;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String deptCode;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String deptName;
}