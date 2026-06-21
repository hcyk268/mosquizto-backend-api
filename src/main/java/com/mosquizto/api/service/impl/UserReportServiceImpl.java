package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.UserReportRequest;
import com.mosquizto.api.dto.response.UserReportResponse;
import com.mosquizto.api.event.dto.UserReportEvent;
import com.mosquizto.api.exception.AccessDeniedException;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.UserReportMapper;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserReport;
import com.mosquizto.api.repository.UserReportRepository;
import com.mosquizto.api.repository.UserRepository;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserReportService;
import com.mosquizto.api.util.UserReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserReportServiceImpl implements UserReportService {

    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;
    private final UserReportRepository userReportRepository;
    private final UserReportMapper userReportMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UserReportResponse reportUser(String username, UserReportRequest request) {
        User reporter = this.currentUserProvider.getCurrentUser();
        User reportedUser = this.userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (reporter.getId().equals(reportedUser.getId())) {
            throw new AccessDeniedException("You cannot report yourself");
        }

        String reason = cleanRequired(request.getReason(), "reason must not be blank");
        String description = cleanOptional(request.getDescription());

        UserReport report = this.userReportRepository
                .findActiveByReporterAndReportedUser(reporter.getId(), reportedUser.getId())
                .orElseGet(() -> UserReport.create(reporter, reportedUser, reason, description));

        report.updateContent(reason, description);
        report = this.userReportRepository.save(report);

        this.eventPublisher.publishEvent(new UserReportEvent(
                report.getId(),
                reportedUser.getUsername()
        ));

        return this.userReportMapper.toResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserReportResponse> getMyPendingReports() {
        Long currentUserId = this.currentUserProvider.getCurrentUser().getId();

        return this.userReportRepository
                .findByReportedUserAndStatus(currentUserId, UserReportStatus.PENDING)
                .stream()
                .map(this.userReportMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void processReport(Long reportId, UserReportStatus status) {
        if (status != UserReportStatus.DISMISSED) {
            throw new InvalidDataException("Only DISMISSED status is allowed");
        }

        Long currentUserId = this.currentUserProvider.getCurrentUser().getId();
        UserReport report = this.userReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        if (!report.getReportedUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission to modify this report");
        }

        report.dismiss();
        this.userReportRepository.save(report);
    }

    private String cleanRequired(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidDataException(message);
        }

        return value.trim();
    }

    private String cleanOptional(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
