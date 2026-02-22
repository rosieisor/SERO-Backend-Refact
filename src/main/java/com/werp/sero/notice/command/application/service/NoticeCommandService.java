package com.werp.sero.notice.command.application.service;

import com.werp.sero.notice.command.application.dto.NoticeCreateRequestDTO;
import com.werp.sero.notice.command.application.dto.NoticeResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoticeCommandService {
    NoticeResponseDTO registerNotice(final int employeeId, final NoticeCreateRequestDTO requestDTO,
                                     final List<MultipartFile> files);

    void deleteNotice(final int employeeId, final int noticeId);
}