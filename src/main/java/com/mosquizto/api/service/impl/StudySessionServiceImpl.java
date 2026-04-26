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
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.IdempotencyService;
import com.mosquizto.api.service.StudySessionService;
import com.mosquizto.api.service.StudySessionStatsCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class StudySessionServiceImpl implements StudySessionService {

    private static final Duration IDEMPOTENCY_RESULT_TTL = Duration.ofMinutes(10);

    private final CurrentUserProvider currentUserProvider;
    private final CollectionService collectionService;
    private final StudySessionRepository studySessionRepository;
    private final StudySessionDetailRepository studySessionDetailRepository;
    private final CollectionItemRepository collectionItemRepository;
    private final StudySessionMapper studySessionMapper;
    private final StudySessionStatsCalculator studySessionStatsCalculator;
    private final IdempotencyService idempotencyService;
    private final PlatformTransactionManager transactionManager;

    @Override
    public Long startStudySession(StartStudySessionRequest startStudySession, String idempotencyKey) {
        User user = this.currentUserProvider.getCurrentUser();
        String fingerprint = this.buildStartStudySessionFingerprint(startStudySession);

        return this.idempotencyService.execute(
                user.getId(),
                idempotencyKey,
                "startStudySession",
                fingerprint,
                IDEMPOTENCY_RESULT_TTL,
                () -> this.executeInTransaction(() -> this.doStartStudySession(startStudySession, user))
        );
    }

    @Override
    public AnswerResultResponse answerItems(Long sessionId, AnswerRequest answerRequest, String idempotencyKey) {
        User user = this.currentUserProvider.getCurrentUser();
        String fingerprint = this.buildAnswerItemsFingerprint(sessionId, answerRequest);

        return this.idempotencyService.execute(
                user.getId(),
                idempotencyKey,
                "answerItems",
                fingerprint,
                IDEMPOTENCY_RESULT_TTL,
                () -> this.executeInTransaction(() -> this.doAnswerItems(sessionId, answerRequest, user))
        );
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
    public StudySessionResultResponse completeStudySession(Long sessionId, String idempotencyKey) {
        User user = this.currentUserProvider.getCurrentUser();
        String fingerprint = this.buildCompleteStudySessionFingerprint(sessionId);

        return this.idempotencyService.execute(
                user.getId(),
                idempotencyKey,
                "completeStudySession",
                fingerprint,
                IDEMPOTENCY_RESULT_TTL,
                () -> this.executeInTransaction(() -> this.doCompleteStudySession(sessionId, user.getUsername()))
        );
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

    private Long doStartStudySession(StartStudySessionRequest startStudySession, User user) {
        Collection collection = this.collectionService.getById(startStudySession.getCollectionId());
        StudySession studySession = StudySession.start(user, collection, new Date());
        studySession = this.studySessionRepository.save(studySession);
        return studySession.getId();
    }

    private AnswerResultResponse doAnswerItems(Long sessionId, AnswerRequest answerRequest, User user) {
        StudySession studySession = this.studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found with id: " + sessionId));

        if (!studySession.isOwnedBy(user.getUsername())) {
            throw new InvalidDataException("You do not have permission to answer in this session");
        }

        Integer collectionId = studySession.getCollection().getId();
        boolean isCorrect;
        String correctAnswer;
        CollectionItem collectionItem;

        if (Boolean.TRUE.equals(answerRequest.getMode())) {
            collectionItem = this.collectionItemRepository.findByCollectionIdAndTerm(collectionId, answerRequest.getTerm())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Collection item not found with term: " + answerRequest.getTerm()));

            correctAnswer = collectionItem.getDefinition();
            isCorrect = correctAnswer != null
                    && correctAnswer.trim().equalsIgnoreCase(answerRequest.getDefinition().trim());
        } else {
            collectionItem = this.collectionItemRepository.findByCollectionIdAndDefinition(collectionId, answerRequest.getDefinition())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Collection item not found with term: " + answerRequest.getDefinition()));

            correctAnswer = collectionItem.getTerm();
            isCorrect = correctAnswer != null
                    && correctAnswer.trim().equalsIgnoreCase(answerRequest.getTerm().trim());
        }

        StudySessionDetail detail = studySession.recordAnswer(
                collectionItem,
                isCorrect,
                answerRequest.getResponseTime(),
                answerRequest.getMode()
        );
        this.studySessionDetailRepository.save(detail);
        this.studySessionRepository.save(studySession);

        return this.studySessionMapper.toAnswerResultResponse(studySession, isCorrect, correctAnswer);
    }

    private StudySessionResultResponse doCompleteStudySession(Long sessionId, String username) {
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

    private String buildStartStudySessionFingerprint(StartStudySessionRequest request) {
        return "collectionId=" + request.getCollectionId();
    }

    private String buildAnswerItemsFingerprint(Long sessionId, AnswerRequest request) {
        return "sessionId=" + sessionId
                + ";mode=" + request.getMode()
                + ";term=" + this.normalizeFingerprintValue(request.getTerm())
                + ";definition=" + this.normalizeFingerprintValue(request.getDefinition())
                + ";responseTime=" + request.getResponseTime();
    }

    private String buildCompleteStudySessionFingerprint(Long sessionId) {
        return "SessionId=" + sessionId;
    }

    private String normalizeFingerprintValue(String value) {
        return value == null ? "" : value.trim();
    }

    private <T> T executeInTransaction(Supplier<T> action) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        return transactionTemplate.execute(status -> action.get());
    }
}
