package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CollectionReportRequest;
import com.mosquizto.api.dto.response.CollectionReportResponse;
import com.mosquizto.api.exception.AccessDeniedException;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CollectionMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionReport;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CollectionReportRepository;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.service.CollectionMembershipResolver;
import com.mosquizto.api.service.CollectionReportService;
import com.mosquizto.api.service.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class CollectionReportServiceImpl implements CollectionReportService {

    private final CurrentUserProvider currentUserProvider;
    private final CollectionRepository collectionRepository;
    private final CollectionReportRepository collectionReportRepository;
    private final CollectionMapper collectionMapper;
    private final CollectionMembershipResolver membershipResolver;

    @Override
    @Transactional
    public CollectionReportResponse reportCollection(Integer collectionId, CollectionReportRequest request) {
        User reporter = this.currentUserProvider.getCurrentUser();
        Collection collection = this.collectionRepository.findActiveById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        membershipResolver.requireCanView(collection, reporter);

        if (collection.isOwnedBy(reporter)) {
            throw new AccessDeniedException("You cannot report your own collection");
        }

        String reason = cleanRequired(request.getReason(), "reason must be not blank");
        String description = cleanOptional(request.getDescription());

        CollectionReport report = this.collectionReportRepository
                .findActiveByCollectionIdAndReporterId(collectionId, reporter.getId())
                .orElseGet(() -> CollectionReport.create(collection, reporter, reason, description));

        report.updateContent(reason, description);

        return this.collectionMapper.toResponse(this.collectionReportRepository.save(report));
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
