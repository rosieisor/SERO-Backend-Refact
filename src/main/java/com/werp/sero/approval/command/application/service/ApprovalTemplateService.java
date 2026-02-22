package com.werp.sero.approval.command.application.service;

import com.werp.sero.approval.command.application.dto.ApprovalTemplateCreateRequestDTO;
import com.werp.sero.approval.command.application.dto.ApprovalTemplateResponseDTO;

public interface ApprovalTemplateService {
    ApprovalTemplateResponseDTO registerApprovalTemplate(final int employeeId,
                                                         final ApprovalTemplateCreateRequestDTO requestDTO);

    void deleteApprovalTemplate(final int employeeId, final int approvalTemplateId);
}