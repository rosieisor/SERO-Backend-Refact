package com.werp.sero.approval.exception;

import com.werp.sero.common.error.ErrorCode;
import com.werp.sero.common.error.exception.BusinessException;

public class ApprovalAlreadyInProgressException extends BusinessException {
    public ApprovalAlreadyInProgressException() {
        super(ErrorCode.APPROVAL_ALREADY_IN_PROGRESS);
    }
}