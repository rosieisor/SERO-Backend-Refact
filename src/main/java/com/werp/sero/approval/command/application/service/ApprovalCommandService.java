package com.werp.sero.approval.command.application.service;

import com.werp.sero.approval.command.application.dto.ApprovalCreateRequestDTO;
import com.werp.sero.approval.command.application.dto.ApprovalDecisionRequestDTO;
import com.werp.sero.approval.command.application.dto.ApprovalResponseDTO;

public interface ApprovalCommandService {
    ApprovalResponseDTO submitForApproval(final int employeeId, final ApprovalCreateRequestDTO requestDTO);

    void approve(final int employeeId, final int approvalId, final ApprovalDecisionRequestDTO requestDTO);

    void reject(final int employeeId, final int approvalId, final ApprovalDecisionRequestDTO requestDTO);
}