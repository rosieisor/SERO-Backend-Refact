package com.werp.sero.order.command.application.service;

import com.werp.sero.order.command.application.dto.SOClientOrderDTO;


public interface SOClientCommandService {
    SOClientOrderDTO createOrder(final int clientEmployeeId, final SOClientOrderDTO request);
}
