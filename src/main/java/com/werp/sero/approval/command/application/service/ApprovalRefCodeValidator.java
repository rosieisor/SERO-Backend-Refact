package com.werp.sero.approval.command.application.service;

public interface ApprovalRefCodeValidator {
    boolean supports(final String targetType);

    Object validate(final String refCode);

    void updateApprovalCodeAndStatus(final String approvalCode, final Object object);

    void approve(final Object ref);

    void reject(final Object ref);

    int getApprovalCode(final Object object);
}