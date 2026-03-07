package com.werp.sero.approval.command.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApprovalAttachRequestDTO {
    @NotBlank
    private String url;

    @NotBlank
    private String fileName;
}