package com.werp.sero.production.command.application.service;

import com.werp.sero.production.command.application.dto.PRDraftCreateRequestDTO;
import com.werp.sero.production.command.application.dto.PRDraftUpdateRequestDTO;

public interface PRCommandService {
    int createDraft(PRDraftCreateRequestDTO dto, int drafterId);

    void updateDraft(int prId, PRDraftUpdateRequestDTO dto, int employeeId);

    void request(int prId, int employeeId);

    void assignManager(int prId, int managerId);

    void updatePRStatusIfNeeded(int prId);
}
