package com.werp.sero.employee.query.controller;

import com.werp.sero.employee.query.dto.EmployeeResponseDTO;
import com.werp.sero.employee.query.service.ClientEmployeeQueryService;
import com.werp.sero.security.annotation.CurrentUser;
import com.werp.sero.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Client Employee", description = "고객사 직원 관련 API")
@RestController
@RequestMapping("/clients/client-employees")
@RequiredArgsConstructor
public class ClientEmployeeController {
    private final ClientEmployeeQueryService clientEmployeeQueryService;

    @Operation(summary = "내 정보 조회 (고객사 직원)")
    @GetMapping("/me")
    public ResponseEntity<EmployeeResponseDTO> findClientEmployeeInfoById(@CurrentUser final CustomUserDetails principal) {
        return ResponseEntity.ok(clientEmployeeQueryService.findClientEmployeeInfoById(principal.getId()));
    }
}