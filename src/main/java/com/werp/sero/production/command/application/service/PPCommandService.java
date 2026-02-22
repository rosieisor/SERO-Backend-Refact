package com.werp.sero.production.command.application.service;

import com.werp.sero.production.command.application.dto.*;

public interface PPCommandService {
    PPValidationResponseDTO validate(PPValidateRequestDTO request);

    PPCreateResponseDTO create(PPCreateRequestDTO request, int employeeId);

    void addToPlanningTarget(PPAddTargetRequestDTO request, int employeeId);
}
