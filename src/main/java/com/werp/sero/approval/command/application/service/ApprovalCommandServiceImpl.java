package com.werp.sero.approval.command.application.service;

import com.werp.sero.approval.command.application.dto.*;
import com.werp.sero.approval.command.domain.aggregate.Approval;
import com.werp.sero.approval.command.domain.aggregate.ApprovalAttachment;
import com.werp.sero.approval.command.domain.aggregate.ApprovalLine;
import com.werp.sero.approval.command.domain.aggregate.enums.ApprovalNotificationType;
import com.werp.sero.approval.command.domain.repository.ApprovalAttachmentRepository;
import com.werp.sero.approval.command.domain.repository.ApprovalLineRepository;
import com.werp.sero.approval.command.domain.repository.ApprovalRepository;
import com.werp.sero.approval.exception.*;
import com.werp.sero.file.service.FileUploader;
import com.werp.sero.common.util.DateTimeUtils;
import com.werp.sero.employee.command.domain.aggregate.Employee;
import com.werp.sero.employee.command.domain.repository.EmployeeRepository;
import com.werp.sero.employee.exception.EmployeeNotFoundException;
import com.werp.sero.notification.command.domain.aggregate.enums.NotificationType;
import com.werp.sero.notification.command.infrastructure.event.NotificationEvent;
import com.werp.sero.system.command.application.service.DocumentSequenceCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ApprovalCommandServiceImpl implements ApprovalCommandService {
    private static final String APPROVAL_DOC_TYPE_CODE = "DOC_SERO";

    private final EmployeeRepository employeeRepository;
    private final ApprovalRepository approvalRepository;
    private final ApprovalLineRepository approvalLineRepository;
    private final ApprovalAttachmentRepository approvalAttachmentRepository;
    private final List<ApprovalRefCodeValidator> approvalRefCodeValidators;
    private final FileUploader fileUploader;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final DocumentSequenceCommandService documentSequenceCommandService;

    @Transactional
    @Override
    public ApprovalResponseDTO submitForApproval(final int employeeId, final ApprovalCreateRequestDTO requestDTO) {
        final Employee employee = findEmployeeById(employeeId);

        checkInProgressApproval(requestDTO.getRefCode());

        validateDuplicateApprovalLineSequence(requestDTO.getApprovers());

        final ApprovalRefCodeValidator validator = findValidator(requestDTO.getApprovalTargetType());

        final Object ref = validator.validate(requestDTO.getRefCode());

        final String approvalCode = documentSequenceCommandService.generateDocumentCode(APPROVAL_DOC_TYPE_CODE);

        final Approval approval = saveApproval(employee, approvalCode, requestDTO);

        saveApprovalAttachments(approval, requestDTO.getAttachments());

        final ApprovalLine firstApproverLine = saveApprovalLinesAndGetFirstApprover(approval, requestDTO.getApprovers());

        saveApprovalReferences(approval, requestDTO.getReferences());

        validator.updateApprovalCodeAndStatus(approvalCode, ref);

        sendApprovalNotification(approval, ApprovalNotificationType.REQUEST, firstApproverLine.getEmployee().getId());

        return ApprovalResponseDTO.of(approval);
    }

    @Transactional
    @Override
    public void approve(final int employeeId, final int approvalId, final ApprovalDecisionRequestDTO requestDTO) {
        final Employee employee = findEmployeeById(employeeId);

        final Approval approval = findApprovalById(approvalId);

        validateApprovalInProgress(approval);

        final ApprovalLine approvalLine = findApprovalLineByApprovalAndEmployee(approval, employee);

        validateCurrentApprover(approval, approvalLine);

        final String documentPrefix = approval.getRefCode().substring(0, 2);

        final ApprovalRefCodeValidator validator = findValidator(documentPrefix);

        final Object ref = validator.validate(approval.getRefCode());

        final String now = DateTimeUtils.nowDateTime();

        approvalLine.updateApprovalLine("ALS_APPR", requestDTO.getNote(), now);

        if (hasNextApprover(approval, approvalLine)) {
            final ApprovalLine nextApprover = activateNextApprover(approval, approvalLine.getSequence());

            sendApprovalNotification(approval, ApprovalNotificationType.REQUEST, nextApprover.getEmployee().getId());

            return;
        }

        validator.approve(ref);

        approval.updateApprovalStatus("AS_APPR", now);

        sendApprovalNotification(approval, ApprovalNotificationType.APPROVED, approval.getEmployee().getId());
    }

    @Transactional
    @Override
    public void reject(final int employeeId, final int approvalId, final ApprovalDecisionRequestDTO requestDTO) {
        final Employee employee = findEmployeeById(employeeId);

        final Approval approval = findApprovalById(approvalId);

        validateApprovalInProgress(approval);

        final ApprovalLine approvalLine = findApprovalLineByApprovalAndEmployee(approval, employee);

        validateCurrentApprover(approval, approvalLine);

        final String documentPrefix = approval.getRefCode().substring(0, 2);

        final ApprovalRefCodeValidator validator = findValidator(documentPrefix);

        final Object ref = validator.validate(approval.getRefCode());

        final String now = DateTimeUtils.nowDateTime();

        validator.reject(ref);

        approvalLine.updateApprovalLine("ALS_RJCT", requestDTO.getNote(), now);

        approval.updateApprovalStatus("AS_RJCT", now);

        sendApprovalNotification(approval, ApprovalNotificationType.REJECTED, approval.getEmployee().getId());
    }

    private void validateApprovalInProgress(final Approval approval) {
        if (!"AS_ING".equals(approval.getStatus())) {
            throw new ApprovalAlreadyProcessedException();
        }
    }

    private void validateCurrentApprover(final Approval approval, final ApprovalLine approvalLine) {
        if (!"ALS_RVW".equals(approvalLine.getStatus())) {
            throw new ApprovalNotCurrentSequenceException();
        }
    }

    private boolean hasNextApprover(final Approval approval, final ApprovalLine approvalLine) {
        return approvalLineRepository.existsByApprovalAndSequenceIsNotNullAndSequenceGreaterThan(approval, approvalLine.getSequence());
    }

    private ApprovalLine activateNextApprover(final Approval approval, final int approvalLineSequence) {
        final ApprovalLine approvalLine =
                approvalLineRepository.findFirstByApprovalAndSequenceGreaterThanOrderBySequenceAsc(approval, approvalLineSequence)
                        .orElseThrow(ApprovalLineRequiredException::new);

        approvalLine.updateStatus("ALS_RVW");

        return approvalLine;
    }

    private void sendApprovalNotification(final Approval approval, final ApprovalNotificationType type,
                                          final int approverId) {
        applicationEventPublisher.publishEvent(new NotificationEvent(
                NotificationType.APPROVAL,
                type.getTitle(approval),
                type.getContent(approval),
                approverId,
                "/approval/" + approval.getId()
        ));
    }

    private ApprovalRefCodeValidator findValidator(final String targetType) {
        return approvalRefCodeValidators.stream()
                .filter(validator -> validator.supports(targetType))
                .findFirst()
                .orElseThrow(InvalidDocumentTypeException::new);
    }

    private ApprovalLine findApprovalLineByApprovalAndEmployee(final Approval approval, final Employee employee) {
        return approvalLineRepository.findByApprovalAndEmployee(approval, employee)
                .orElseThrow(ApprovalLineAccessDeniedException::new);
    }

    private Approval findApprovalById(final int approvalId) {

        return approvalRepository.findById(approvalId)
                .orElseThrow(ApprovalNotFoundException::new);
    }

    private void checkInProgressApproval(final String refCode) {
        if (approvalRepository.existsByRefCodeAndStatus(refCode, "AS_ING")) {
            throw new ApprovalAlreadyInProgressException();
        }
    }

    private Approval saveApproval(final Employee employee, final String approvalCode,
                                  final ApprovalCreateRequestDTO requestDTO) {
        final int totalLine = requestDTO.getApprovers().size();

        final Approval approval = new Approval(approvalCode, requestDTO.getTitle(), requestDTO.getContent(),
                totalLine, requestDTO.getRefCode(), DateTimeUtils.nowDateTime(), employee);

        return approvalRepository.save(approval);
    }

    private void saveApprovalAttachments(final Approval approval, final List<ApprovalAttachRequestDTO> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        final List<ApprovalAttachment> approvalAttachments = files.stream()
                .map(file -> {
                    final String s3Url = fileUploader.copyObject("documents/", file.getUrl());

                    return new ApprovalAttachment(file.getFileName(), s3Url, approval);
                })
                .collect(Collectors.toList());

        approvalAttachmentRepository.saveAll(approvalAttachments);
    }

    private ApprovalLine saveApprovalLinesAndGetFirstApprover(final Approval approval,
                                                              final List<ApprovalLineRequestDTO> requestDTOs) {
        final Map<Integer, Employee> employeeMap = findEmployeeMap(
                requestDTOs.stream().map(ApprovalLineRequestDTO::getApproverId).collect(Collectors.toList()));

        final int firstSequence = requestDTOs.stream()
                .mapToInt(ApprovalLineRequestDTO::getSequence)
                .min()
                .orElseThrow(ApprovalLineRequiredException::new);

        List<ApprovalLine> approvalLines = requestDTOs.stream()
                .map(dto -> {
                    final Employee employee = findEmployeeFromMap(employeeMap, dto.getApproverId());

                    final String status = (dto.getSequence() == firstSequence) ? "ALS_RVW" : "ALS_PEND";

                    return new ApprovalLine(dto.getLineType().name(), dto.getSequence(), status, approval, employee);
                })
                .collect(Collectors.toList());

        approvalLines = approvalLineRepository.saveAll(approvalLines);

        return approvalLines.stream()
                .min(Comparator.comparingInt(ApprovalLine::getSequence))
                .orElseThrow(ApprovalLineRequiredException::new);
    }

    private void saveApprovalReferences(final Approval approval, final List<ApprovalReferenceRequestDTO> references) {
        if (references == null || references.isEmpty()) {
            return;
        }

        final Map<Integer, Employee> employeeMap = findEmployeeMap(
                references.stream().map(ApprovalReferenceRequestDTO::getReferenceId).collect(Collectors.toList()));

        final List<ApprovalLine> observerLines = references.stream()
                .map(dto -> {
                    final Employee employee = findEmployeeFromMap(employeeMap, dto.getReferenceId());

                    return new ApprovalLine(dto.getLineType().name(), null, null, approval, employee);
                })
                .collect(Collectors.toList());

        approvalLineRepository.saveAll(observerLines);
    }

    private void validateDuplicateApprovalLineSequence(final List<ApprovalLineRequestDTO> requestDTOs) {
        final Set<Integer> sequenceSet = new HashSet<>();

        for (final ApprovalLineRequestDTO dto : requestDTOs) {
            if (!sequenceSet.add(dto.getSequence())) {
                throw new ApprovalLineSequenceDuplicatedException();
            }
        }
    }

    private Map<Integer, Employee> findEmployeeMap(final List<Integer> employeeIds) {
        return employeeRepository.findByIdIn(employeeIds).stream()
                .collect(Collectors.toMap(Employee::getId, employee -> employee));
    }

    private Employee findEmployeeFromMap(final Map<Integer, Employee> employeeMap, final int employeeId) {
        final Employee employee = employeeMap.get(employeeId);

        if (employee == null) {
            throw new EmployeeNotFoundException(employeeId + "번의 직원이 존재하지 않습니다.");
        }

        return employee;
    }

    private Employee findEmployeeById(final int employeeId) {
        return employeeRepository.findByIdAndStatus(employeeId, "ES_ACT").orElseThrow(EmployeeNotFoundException::new);
    }
}