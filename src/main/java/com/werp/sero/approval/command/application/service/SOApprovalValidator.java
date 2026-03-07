package com.werp.sero.approval.command.application.service;

import com.werp.sero.approval.exception.ApprovalNotSubmittedException;
import com.werp.sero.approval.exception.ApprovalRefDocumentAlreadyProcessedException;
import com.werp.sero.notification.command.domain.aggregate.enums.NotificationType;
import com.werp.sero.notification.command.infrastructure.event.NotificationEvent;
import com.werp.sero.order.command.domain.aggregate.SalesOrder;
import com.werp.sero.order.command.domain.repository.SORepository;
import com.werp.sero.order.exception.SalesOrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SOApprovalValidator implements ApprovalRefCodeValidator {
    private static final String TYPE = "SO";

    private final SORepository soRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public boolean supports(final String targetType) {
        return TYPE.equals(targetType);
    }

    @Override
    public SalesOrder validate(final String refCode) {
        return soRepository.findBySoCode(refCode)
                .orElseThrow(SalesOrderNotFoundException::new);
    }

    @Override
    public void updateApprovalCodeAndStatus(final String approvalCode, final Object object) {
        ((SalesOrder) object).updateApprovalInfo(approvalCode, "ORD_APPR_PEND");
    }

    @Override
    public void approve(final Object ref) {
        final SalesOrder so = (SalesOrder) ref;

        validatePendingState(so);

        so.updateApprovalInfo(so.getApprovalCode(), "ORD_APPR_DONE");

        applicationEventPublisher.publishEvent(NotificationEvent.forClient(
                NotificationType.ORDER,
                "주문 상태 변경",
                "주문번호 " + so.getSoCode() + "의 상태가 진행중으로 변경되었습니다.",
                so.getClientEmployee().getId(),
                "/client-portal/orders/" + so.getId()
        ));
    }

    @Override
    public void reject(final Object ref) {
        final SalesOrder so = (SalesOrder) ref;

        validatePendingState(so);

        so.updateApprovalInfo(so.getApprovalCode(), "ORD_APPR_RJCT");
    }

    private void validatePendingState(final SalesOrder so) {
        if (so.getApprovalCode() == null) {
            throw new ApprovalNotSubmittedException();
        }

        if (!"ORD_APPR_PEND".equals(so.getStatus())) {
            throw new ApprovalRefDocumentAlreadyProcessedException();
        }
    }

    @Override
    public int getApprovalCode(final Object object) {
        return ((SalesOrder) object).getId();
    }
}