package com.werp.sero.approval.command.application.dto;

import com.werp.sero.approval.command.domain.aggregate.enums.ApprovalReferenceLineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApprovalReferenceRequestDTO {
    @Schema(description = "결재자 ID(PK)")
    private int referenceId;

    @Schema(description = "결재 유형")
    @NotNull
    private ApprovalReferenceLineType lineType;
}