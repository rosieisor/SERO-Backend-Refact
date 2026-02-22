package com.werp.sero.shipping.command.application.service;

public interface DeliveryCommandService {

    /**
     * Start delivery - Update status to SHIP_ING
     * @param giCode 출고지시 코드
     * @param driverId 기사 ID
     */
    void startDelivery(String giCode, int driverId);

    /**
     * Complete delivery - Update status to SHIP_DONE
     * @param giCode 출고지시 코드
     * @param driverId 기사 ID
     */
    void completeDelivery(String giCode, int driverId);
}
