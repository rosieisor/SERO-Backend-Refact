package com.werp.sero.approval.command.application.service;

import com.werp.sero.approval.exception.ApprovalNotSubmittedException;
import com.werp.sero.approval.exception.ApprovalRefDocumentAlreadyProcessedException;
import com.werp.sero.order.command.domain.aggregate.SalesOrder;
import com.werp.sero.shipping.command.domain.aggregate.GoodsIssue;
import com.werp.sero.shipping.command.domain.repository.GIRepository;
import com.werp.sero.shipping.exception.GoodsIssueNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GIApprovalValidator implements ApprovalRefCodeValidator {
    private static final String TYPE = "GI";

    private final GIRepository giRepository;

    @Override
    public boolean supports(final String targetType) {
        return TYPE.equals(targetType);
    }

    @Override
    public GoodsIssue validate(final String refCode) {
        return giRepository.findByGiCode(refCode)
                .orElseThrow(GoodsIssueNotFoundException::new);
    }

    @Override
    public void updateApprovalCodeAndStatus(final String approvalCode, final Object object) {
        ((GoodsIssue) object).updateApprovalInfo(approvalCode, "GI_APPR_PEND");
    }

    @Override
    public void approve(final Object ref) {
        final GoodsIssue gi = (GoodsIssue) ref;

        validatePendingState(gi);

        gi.updateApprovalInfo(gi.getApprovalCode(), "GI_APPR_DONE");

        final SalesOrder salesOrder = gi.getSalesOrder();

        if ("ORD_APPR_DONE".equals(salesOrder.getStatus())) {
            salesOrder.updateApprovalInfo(salesOrder.getApprovalCode(), "ORD_SHIP_READY");
        }
    }

    @Override
    public void reject(final Object ref) {
        final GoodsIssue gi = (GoodsIssue) ref;

        validatePendingState(gi);

        gi.updateApprovalInfo(gi.getApprovalCode(), "GI_APPR_RJCT");
    }

    private void validatePendingState(final GoodsIssue gi) {
        if (gi.getApprovalCode() == null) {
            throw new ApprovalNotSubmittedException();
        }

        if (!"GI_APPR_PEND".equals(gi.getStatus())) {
            throw new ApprovalRefDocumentAlreadyProcessedException();
        }
    }

    @Override
    public int getApprovalCode(final Object object) {
        return ((GoodsIssue) object).getId();
    }
}
