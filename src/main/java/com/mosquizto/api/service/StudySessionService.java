package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.AnswerRequest;
import com.mosquizto.api.dto.request.StartStudySessionRequest;
import com.mosquizto.api.dto.response.*;
import jakarta.validation.Valid;

public interface StudySessionService {
    Long startStudySession(StartStudySessionRequest startStudySession, String idempotencyKey);

    AnswerResultResponse answerItems(Long sessionId, AnswerRequest answerRequest, String idempotencyKey);

    StudySessionDetailsResponse getSessionDetails(Long sessionId);

    StudySessionResultResponse completeStudySession(Long sessionId, String idempotencyKey);

    PageResponse<StudySessionResponse> getHistoryStudy(int page, int size);

    StudySessionStatsResponse getStudySessionStats(Integer collectionId);
}
