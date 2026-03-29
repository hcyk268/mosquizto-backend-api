package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.AnswerRequest;
import com.mosquizto.api.dto.request.StartStudySessionRequest;
import com.mosquizto.api.dto.response.*;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.StudySessionMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.StudySession;
import com.mosquizto.api.model.StudySessionDetail;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.StudySessionDetailRepository;
import com.mosquizto.api.repository.StudySessionRepository;
import com.mosquizto.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StudySessionServiceImpl implements StudySessionService {

    private final CurrentUserProvider currentUserProvider;
    private final CollectionService collectionService;
    private final StudySessionRepository studySessionRepository;
    private final StudySessionDetailRepository studySessionDetailRepository;
    private final CollectionItemRepository collectionItemRepository;
    private final StudySessionMapper studySessionMapper;
    private final StudySessionAuthorizationService studySessionAuthorizationService;
    private final StudySessionStatsCalculator studySessionStatsCalculator;

    @Override
    public Long startStudySession(StartStudySessionRequest startStudySession) {

        User user = this.currentUserProvider.getCurrentUser();
        Collection collection = this.collectionService.getById(startStudySession.getCollectionId());

        StudySession studySession = StudySession.start(user, collection, new Date());

        studySession = this.studySessionRepository.save(studySession);

        return studySession.getId();
    }

    @Override
    @Transactional
    public AnswerResultResponse answerItems(Long sessionId, AnswerRequest answerRequest) {
        String username = this.currentUserProvider.getCurrentUsername();

        StudySession studySession = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found with id: " + sessionId));

        if (!studySession.isOwnedBy(username)) {
            throw new InvalidDataException("You do not have permission to answer in this session");
        }

        Integer collectionId = studySession.getCollection().getId();
        CollectionItem collectionItem = collectionItemRepository.findByCollectionIdAndTerm(collectionId, answerRequest.getTerm())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Collection item not found with term: " + answerRequest.getTerm()));

        String correctDefinition = collectionItem.getDefinition();
        boolean isCorrect = correctDefinition != null
                && correctDefinition.trim().equalsIgnoreCase(answerRequest.getDefinition().trim());

        StudySessionDetail detail = studySession.recordAnswer(
                collectionItem,
                isCorrect,
                answerRequest.getResponseTime()
        );
        studySessionDetailRepository.save(detail);
        studySessionRepository.save(studySession);

        return this.studySessionMapper.toAnswerResultResponse(studySession, isCorrect, correctDefinition);
    }

    @Override
    @Transactional(readOnly = true)
    public StudySessionDetailsResponse getSessionDetails(Long sessionId) {
        String username = this.currentUserProvider.getCurrentUsername();

        StudySession studySession = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found with id: " + sessionId));

        if (!studySession.isOwnedBy(username)) {
            throw new InvalidDataException("You do not have permission to view in this session");
        }

        List<StudySessionDetail> details = studySessionDetailRepository
                .findAllByStudySessionIdOrderByIdAsc(sessionId);

        return this.studySessionMapper.toDetailsResponse(studySession, details);
    }

    @Override
    @Transactional
    public StudySessionResultResponse completeStudySession(Long sessionId) {
        String username = this.currentUserProvider.getCurrentUsername();

        StudySession studySession = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found with id: " + sessionId));

        if (!studySession.isOwnedBy(username)) {
            throw new InvalidDataException("You do not have permission to complete in this session");
        }

        Date now = new Date();
        studySession.complete(now);
        studySessionRepository.save(studySession);

        long durationMs = studySession.calculateDurationMs();
        double accuracyRate = studySession.calculateAccuracyRate();

        return this.studySessionMapper.toResultResponse(studySession, accuracyRate, durationMs);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudySessionResponse> getHistoryStudy(int page, int size) {
        if (page < 1) {
            throw new InvalidDataException("Page must be greater than or equal to 1");
        }

        if (size < 1) {
            throw new InvalidDataException("Size must be greater than or equal to 1");
        }

        User user = this.currentUserProvider.getCurrentUser();

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "startedAt"));

        Page<StudySession> studySessionPage = this.studySessionRepository.findAllByUserId(user.getId(), pageable);

        List<StudySessionResponse> studySessionResponses = studySessionPage.getContent().stream()
                .map(this.studySessionMapper::toResponse)
                .toList();

        return PageResponse.<StudySessionResponse>builder()
                .page(page)
                .size(size)
                .totalElements(studySessionPage.getTotalElements())
                .totalPages(studySessionPage.getTotalPages())
                .items(studySessionResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public StudySessionStatsResponse getStudySessionStats(Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();
        Collection collection = this.collectionService.getById(collectionId);

        List<StudySession> sessions = this.studySessionRepository.findAllByUserIdAndCollectionId(user.getId(), collectionId);

        int totalCorrect = this.studySessionStatsCalculator.getTotalCorect(sessions);
        int totalWrong = this.studySessionStatsCalculator.getTotalWrong(sessions);
        int bestScore = this.studySessionStatsCalculator.getBestScore(sessions);
        double averageAccuracyRate = this.studySessionStatsCalculator.getAverageAccuracyRate(sessions);
        long averageDurationMs = this.studySessionStatsCalculator.getAverageDurationMs(sessions);
        Date lastStudiedAt = this.studySessionStatsCalculator.getLastStudiedAt(sessions);

        return this.studySessionMapper.toStatsResponse(
                collection,
                sessions.size(),
                totalCorrect,
                totalWrong,
                averageAccuracyRate,
                bestScore,
                averageDurationMs,
                lastStudiedAt
        );
    }

}
