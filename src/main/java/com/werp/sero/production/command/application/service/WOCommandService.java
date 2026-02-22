package com.werp.sero.production.command.application.service;

import com.werp.sero.production.command.application.dto.WorkOrderCreateRequestDTO;
import com.werp.sero.production.command.application.dto.WorkOrderEndRequest;
import com.werp.sero.production.command.application.dto.WorkOrderResultPreviewRequestDTO;
import com.werp.sero.production.command.application.dto.WorkOrderResultPreviewResponseDTO;

public interface WOCommandService {

    void createWorkOrder(WorkOrderCreateRequestDTO request, int employeeId);

    void start(int woId, String note, int employeeId);

    void pause(int woId, String note);

    void resume(int woId, String note);

    void end(int woId, WorkOrderEndRequest request, int employeeId);

    WorkOrderResultPreviewResponseDTO previewResult(int woId, WorkOrderResultPreviewRequestDTO request);
}
