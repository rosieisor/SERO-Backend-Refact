package com.werp.sero.approval.command.application.service;

import com.werp.sero.approval.exception.ApprovalNotSubmittedException;
import com.werp.sero.approval.exception.ApprovalRefDocumentAlreadyProcessedException;
import com.werp.sero.production.command.domain.aggregate.ProductionRequest;
import com.werp.sero.production.command.domain.repository.PRRepository;
import com.werp.sero.production.exception.ProductionRequestNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PRApprovalValidator implements ApprovalRefCodeValidator {
    private static final String TYPE = "PR";

    private final PRRepository prRepository;

    @Override
    public boolean supports(final String targetType) {
        return TYPE.equals(targetType);
    }

    @Override
    public ProductionRequest validate(final String refCode) {
        return prRepository.findByPrCode(refCode)
                .orElseThrow(ProductionRequestNotFoundException::new);
    }

    @Override
    public void updateApprovalCodeAndStatus(final String approvalCode, final Object object) {
        ((ProductionRequest) object).updateApprovalInfo(approvalCode, "PR_APPR_PEND");
    }

    @Override
    public void approve(final Object ref) {
        final ProductionRequest pr = (ProductionRequest) ref;

        validatePendingState(pr);

        pr.updateApprovalInfo(pr.getApprovalCode(), "PR_APPR_DONE");
    }

    @Override
    public void reject(final Object ref) {
        final ProductionRequest pr = (ProductionRequest) ref;

        validatePendingState(pr);

        pr.updateApprovalInfo(pr.getApprovalCode(), "PR_APPR_RJCT");
    }

    private void validatePendingState(final ProductionRequest pr) {
        if (pr.getApprovalCode() == null) {
            throw new ApprovalNotSubmittedException();
        }

        if (!"PR_APPR_PEND".equals(pr.getStatus())) {
            throw new ApprovalRefDocumentAlreadyProcessedException();
        }
    }

    @Override
    public int getApprovalCode(final Object object) {
        return ((ProductionRequest) object).getId();
    }
}