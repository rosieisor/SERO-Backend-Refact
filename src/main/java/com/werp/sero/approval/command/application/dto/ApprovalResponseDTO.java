package com.werp.sero.approval.command.application.dto;

import com.werp.sero.approval.command.domain.aggregate.Approval;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalResponseDTO {
    @Schema(description = "결재 ID(PK)")
    private int approvalId;

    @Schema(description = "결재 코드")
    private String approvalCode;

    @Schema(description = "기안자 ID(PK)")
    private int drafterId;

    public static ApprovalResponseDTO of(final Approval approval) {
        return ApprovalResponseDTO.builder()
                .approvalId(approval.getId())
                .approvalCode(approval.getApprovalCode())
                .drafterId(approval.getEmployee().getId())
                .build();
    }
}