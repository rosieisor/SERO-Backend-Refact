package com.werp.sero.approval.query.service;

import com.werp.sero.approval.query.dto.*;
import org.springframework.data.domain.Pageable;

public interface ApprovalQueryService {
    ApprovalListResponseDTO getSubmittedApprovals(final int employeeId,
                                                  final SubmittedApprovalFilterRequestDTO filterDTO,
                                                  final Pageable pageable);

    ApprovalListResponseDTO getArchivedApprovals(final int employeeId,
                                                 final ArchivedApprovalFilterRequestDTO filterDTO,
                                                 final Pageable pageable);

    ApprovalListResponseDTO getReceivedApprovals(final int employeeId,
                                                 final ReceivedApprovalFilterRequestDTO filterDTO,
                                                 final Pageable pageable);

    ApprovalListResponseDTO getReferencedApprovals(final int employeeId,
                                                   final ReferencedApprovalFilterRequestDTO filterDTO,
                                                   final Pageable pageable);

    ApprovalListResponseDTO getRequestedApprovals(final int employeeId,
                                                  final RequestedApprovalFilterRequestDTO filterDTO,
                                                  final Pageable pageable);

    ApprovalDetailResponseDTO getApprovalInfo(final int employeeId, final int approvalId);

    ApprovalLineSummaryInfoResponseDTO getApprovalSummaryInfo(final String approvalCode);
}