package com.werp.sero.order.query.service;

import com.werp.sero.order.query.dto.SOClientDetailResponseDTO;
import com.werp.sero.order.query.dto.SOClientFilterDTO;
import com.werp.sero.order.query.dto.SOClientListResponseDTO;
import com.werp.sero.order.query.dto.SOClientResponseDTO;

import java.util.List;

public interface SOClientQueryService {
    List<SOClientResponseDTO> findOrderHistory(final int clientId);

    SOClientResponseDTO getOrderForReorder(final int orderId, final int clientId);

    List<SOClientListResponseDTO> findClientOrderList(final int clientId, final SOClientFilterDTO filter, final Integer page);

    SOClientDetailResponseDTO findClientOrderDetail(final int orderId, final int clientId);
}
