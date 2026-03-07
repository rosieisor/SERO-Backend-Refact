package com.werp.sero.file.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PresignedUrlRequest {
    @NotBlank
    private String fileName;

    @NotBlank
    private String contentType;
}