package com.werp.sero.approval.query.service;

import com.werp.sero.approval.query.dto.ApprovalTemplateInfoResponseDTO;

import java.util.List;

public interface ApprovalTemplateQueryService {
    List<ApprovalTemplateInfoResponseDTO> getApprovalTemplates(final int employeeId);

    ApprovalTemplateInfoResponseDTO getApprovalTemplateById(final int employeeId, final int approvalTemplateId);
}