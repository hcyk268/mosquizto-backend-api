package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.AnswerRequest;
import com.mosquizto.api.dto.request.StartStudySessionRequest;
import com.mosquizto.api.dto.response.*;
import com.mosquizto.api.dto.response.StudySessionDetailsResponse.StudySessionAnswerDetailResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.StudySession;
import com.mosquizto.api.model.StudySessionDetail;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.StudySessionDetailRepository;
import com.mosquizto.api.repository.StudySessionRepository;
import com.mosquizto.api.service.*;
import com.mosquizto.api.util.TokenType;
import jakarta.servlet.http.HttpServletRequest;
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

    private final AuthenticatedUserService authenticatedUserService;
    private final CollectionService collectionService;
    private final StudySessionRepository studySessionRepository;
    private final StudySessionDetailRepository studySessionDetailRepository;
    private final CollectionItemRepository collectionItemRepository;
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public Long startStudySession(StartStudySessionRequest startStudySession, HttpServletRequest request) {

        User user = this.authenticatedUserService.getAuthenticatedUser(request);
        Collection collection = this.collectionService.getById(startStudySession.getCollectionId());

        StudySession studySession = StudySession.builder()
                .user(user)
                .collection(collection)
                .totalScore(0)
                .totalWrong(0)
                .totalCorrect(0)
                .startedAt(new Date())
                .build();

        studySession = this.studySessionRepository.save(studySession);

        return studySession.getId();
    }

    @Override
    @Transactional
    public AnswerResultResponse answerItems(String token, Long sessionId, AnswerRequest answerRequest) {
        String username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);

        StudySession studySession = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found with id: " + sessionId));

        if (!studySession.getUser().getUsername().equals(username)) {
            throw new InvalidDataException("You do not have permission to answer in this session");
        }

        if (studySession.getCompletedAt() != null) {
            throw new InvalidDataException("This study session has already been completed");
        }

        Integer collectionId = studySession.getCollection().getId();
        CollectionItem collectionItem = collectionItemRepository.findByCollectionIdAndTerm(collectionId, answerRequest.getTerm())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Collection item not found with term: " + answerRequest.getTerm()));

        String correctDefinition = collectionItem.getDefinition();
        boolean isCorrect = correctDefinition != null
                && correctDefinition.trim().equalsIgnoreCase(answerRequest.getDefinition().trim());

        StudySessionDetail detail = StudySessionDetail.builder()
                .studySession(studySession)
                .collectionItem(collectionItem)
                .isCorrect(isCorrect)
                .responseTimeMs(answerRequest.getResponseTime())
                .build();
        studySessionDetailRepository.save(detail);

        if (isCorrect) {
            studySession.setTotalCorrect(studySession.getTotalCorrect() + 1);
            studySession.setTotalScore(studySession.getTotalScore() + 1);
        } else {
            studySession.setTotalWrong(studySession.getTotalWrong() + 1);
        }
        studySessionRepository.save(studySession);

        return AnswerResultResponse.builder()
                .isCorrect(isCorrect)
                .correctAnswer(correctDefinition)
                .sessionId(sessionId)
                .totalScore(studySession.getTotalScore())
                .totalCorrect(studySession.getTotalCorrect())
                .totalWrong(studySession.getTotalWrong())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public StudySessionDetailsResponse getSessionDetails(String token, Long sessionId) {
        String username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);

        StudySession studySession = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found with id: " + sessionId));

        if (!studySession.getUser().getUsername().equals(username)) {
            throw new InvalidDataException("You do not have permission to view this session");
        }

        List<StudySessionAnswerDetailResponse> details = studySessionDetailRepository
                .findAllByStudySessionIdOrderByIdAsc(sessionId)
                .stream()
                .map(detail -> StudySessionAnswerDetailResponse.builder()
                        .detailId(detail.getId())
                        .collectionItemId(detail.getCollectionItem().getId())
                        .term(detail.getCollectionItem().getTerm())
                        .correctAnswer(detail.getCollectionItem().getDefinition())
                        .isCorrect(detail.getIsCorrect())
                        .responseTimeMs(detail.getResponseTimeMs())
                        .build())
                .toList();

        return StudySessionDetailsResponse.builder()
                .sessionId(studySession.getId())
                .collectionId(studySession.getCollection().getId())
                .collectionName(studySession.getCollection().getTitle())
                .startedAt(studySession.getStartedAt())
                .completedAt(studySession.getCompletedAt())
                .totalScore(studySession.getTotalScore())
                .totalCorrect(studySession.getTotalCorrect())
                .totalWrong(studySession.getTotalWrong())
                .details(details)
                .build();
    }

    @Override
    @Transactional
    public StudySessionResultResponse completeStudySession(String token, Long sessionId) {
        String username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);

        StudySession studySession = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found with id: " + sessionId));

        if (!studySession.getUser().getUsername().equals(username)) {
            throw new InvalidDataException("You do not have permission to complete this session");
        }

        if (studySession.getCompletedAt() != null) {
            throw new InvalidDataException("This study session has already been completed");
        }

        Date now = new Date();
        studySession.setCompletedAt(now);
        studySessionRepository.save(studySession);

        long durationMs = now.getTime() - studySession.getStartedAt().getTime();
        int totalAnswered = studySession.getTotalCorrect() + studySession.getTotalWrong();
        double accuracyRate = totalAnswered > 0 ? (double) studySession.getTotalCorrect() / totalAnswered * 100 : 0.0;

        return StudySessionResultResponse.builder()
                .sessionId(sessionId)
                .totalScore(studySession.getTotalScore())
                .totalCorrect(studySession.getTotalCorrect())
                .totalWrong(studySession.getTotalWrong())
                .accuracyRate(accuracyRate)
                .durationMs(durationMs)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudySessionResponse> getHistoryStudy(String token, int page, int size) {
        if (page < 1) {
            throw new InvalidDataException("Page must be greater than or equal to 1");
        }

        if (size < 1) {
            throw new InvalidDataException("Size must be greater than or equal to 1");
        }

        String username = this.jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);

        User user = this.userService.getByUsername(username);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "startedAt"));

        Page<StudySession> studySessionPage = this.studySessionRepository.findAllByUserId(user.getId(), pageable);

        List<StudySessionResponse> studySessionResponses = studySessionPage.getContent().stream().map(
                st -> StudySessionResponse.builder()
                        .sessionId(st.getId())
                        .collectionName(st.getCollection().getTitle())
                        .totalScore(st.getTotalScore())
                        .totalWrong(st.getTotalWrong())
                        .totalCorrect(st.getTotalCorrect())
                        .startedAt(st.getStartedAt())
                        .completedAt(st.getCompletedAt())
                        .build()
                    ).toList();

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
    public StudySessionStatsResponse getStudySessionStats(String token, Integer collectionId) {
        String username = this.jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        User user = this.userService.getByUsername(username);
        Collection collection = this.collectionService.getById(collectionId);

        List<StudySession> sessions = this.studySessionRepository.findAllByUserIdAndCollectionId(user.getId(), collectionId);

        int totalCorrect = sessions.stream()
                .mapToInt(session -> session.getTotalCorrect() == null ? 0 : session.getTotalCorrect())
                .sum();
        int totalWrong = sessions.stream()
                .mapToInt(session -> session.getTotalWrong() == null ? 0 : session.getTotalWrong())
                .sum();
        int bestScore = sessions.stream()
                .mapToInt(session -> session.getTotalScore() == null ? 0 : session.getTotalScore())
                .max()
                .orElse(0);

        double averageAccuracyRate = sessions.stream()
                .mapToDouble(this::calculateSessionAccuracyRate)
                .average()
                .orElse(0.0);

        long averageDurationMs = (long) sessions.stream()
                .filter(session -> session.getStartedAt() != null && session.getCompletedAt() != null)
                .mapToLong(session -> session.getCompletedAt().getTime() - session.getStartedAt().getTime())
                .average()
                .orElse(0.0);

        Date lastStudiedAt = sessions.stream()
                .map(StudySession::getStartedAt)
                .filter(java.util.Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);

        return StudySessionStatsResponse.builder()
                .collectionId(collection.getId())
                .collectionName(collection.getTitle())
                .totalSessions((long) sessions.size())
                .totalCorrect(totalCorrect)
                .totalWrong(totalWrong)
                .averageAccuracyRate(averageAccuracyRate)
                .bestScore(bestScore)
                .averageDurationMs(averageDurationMs)
                .lastStudiedAt(lastStudiedAt)
                .build();
    }

    private double calculateSessionAccuracyRate(StudySession session) {
        int totalCorrect = session.getTotalCorrect() == null ? 0 : session.getTotalCorrect();
        int totalWrong = session.getTotalWrong() == null ? 0 : session.getTotalWrong();
        int totalAnswered = totalCorrect + totalWrong;

        if (totalAnswered == 0) {
            return 0.0;
        }

        return (double) totalCorrect / totalAnswered * 100;
    }
}
