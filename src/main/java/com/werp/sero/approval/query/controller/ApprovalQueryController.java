package com.werp.sero.approval.query.controller;

import com.werp.sero.approval.query.dto.*;
import com.werp.sero.approval.query.service.ApprovalQueryService;
import com.werp.sero.security.annotation.CurrentUser;
import com.werp.sero.security.principal.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "결재 - Query", description = "결재 관련 조회 API")
@RequestMapping("/approvals")
@RequiredArgsConstructor
@RestController
public class ApprovalQueryController {
    private final ApprovalQueryService approvalQueryService;

    @Operation(summary = "기안함 목록 조회", description = "본인이 상신한 결재 목록을 조회합니다.")
    @GetMapping("/submitted")
    public ResponseEntity<ApprovalListResponseDTO> getSubmittedApprovals(@CurrentUser final CustomUserDetails user,
                                                                         @ModelAttribute final SubmittedApprovalFilterRequestDTO filterDTO,
                                                                         @PageableDefault(sort = "id", direction = Sort.Direction.DESC) final Pageable pageable) {
        return ResponseEntity.ok(approvalQueryService.getSubmittedApprovals(user.getId(), filterDTO, pageable));
    }

    @Operation(summary = "결재함 목록 조회", description = "본인이 결재 처리(승인 또는 반려)를 완료한 결재 목록을 조회합니다.")
    @GetMapping("/archived")
    public ResponseEntity<ApprovalListResponseDTO> getArchivedApprovals(@CurrentUser final CustomUserDetails user,
                                                                        @ModelAttribute ArchivedApprovalFilterRequestDTO filterDTO,
                                                                        @PageableDefault(sort = "id", direction = Sort.Direction.DESC) final Pageable pageable) {
        return ResponseEntity.ok(approvalQueryService.getArchivedApprovals(user.getId(), filterDTO, pageable));
    }

    @Operation(summary = "결재 요청함 목록 조회", description = "결재선에 본인이 포함되어 있으며, 현재 결재 차례가 본인인 결재 목록을 조회합니다.")
    @GetMapping("/requested")
    public ResponseEntity<ApprovalListResponseDTO> getRequestedApprovals(@CurrentUser final CustomUserDetails user,
                                                                         @ModelAttribute RequestedApprovalFilterRequestDTO filterDTO,
                                                                         @PageableDefault(sort = "id", direction = Sort.Direction.DESC) final Pageable pageable) {
        return ResponseEntity.ok(approvalQueryService.getRequestedApprovals(user.getId(), filterDTO, pageable));
    }

    @Operation(summary = "수신함 목록 조회",
            description = "결재선에 본인이 포함되어 있으며, 결재선 타입이 수신이고 최종 승인 완료된 결재 목록을 조회합니다.")
    @GetMapping("/received")
    public ResponseEntity<ApprovalListResponseDTO> getReceivedApprovals(@CurrentUser final CustomUserDetails user,
                                                                        @ModelAttribute ReceivedApprovalFilterRequestDTO filterDTO,
                                                                        @PageableDefault(sort = "id", direction = Sort.Direction.DESC) final Pageable pageable) {
        return ResponseEntity.ok(approvalQueryService.getReceivedApprovals(user.getId(), filterDTO, pageable));
    }

    @Operation(summary = "참조함 목록 조회", description = "결재선에 본인이 포함되어 있고, 결재선 타입이 참조인 결재 목록을 조회합니다.")
    @GetMapping("/referenced")
    public ResponseEntity<ApprovalListResponseDTO> getReferencedApprovals(@CurrentUser final CustomUserDetails user,
                                                                          @ModelAttribute ReferencedApprovalFilterRequestDTO filterDTO,
                                                                          @PageableDefault(sort = "id", direction = Sort.Direction.DESC) final Pageable pageable) {
        return ResponseEntity.ok(approvalQueryService.getReferencedApprovals(user.getId(), filterDTO, pageable));
    }

    @Operation(summary = "결재 상세 조회")
    @GetMapping("/{approvalId}")
    public ResponseEntity<ApprovalDetailResponseDTO> getApprovalInfo(@CurrentUser final CustomUserDetails user,
                                                                     @PathVariable("approvalId") final int approvalId) {
        return ResponseEntity.ok(approvalQueryService.getApprovalInfo(user.getId(), approvalId));
    }

    @Operation(summary = "결재에 대한 결재선 요약 조회")
    @GetMapping("/summary/{approvalCode}")
    public ResponseEntity<ApprovalLineSummaryInfoResponseDTO> getApprovalSummaryInfo(@PathVariable("approvalCode") final String approvalCode) {
        return ResponseEntity.ok(approvalQueryService.getApprovalSummaryInfo(approvalCode));
    }
}
