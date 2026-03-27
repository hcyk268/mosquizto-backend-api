package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.AnswerRequest;
import com.mosquizto.api.dto.request.StartStudySessionRequest;
import com.mosquizto.api.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface StudySessionService {
    Long startStudySession(StartStudySessionRequest startStudySession, HttpServletRequest request);

    AnswerResultResponse answerItems(String token, Long sessionId, @Valid AnswerRequest answerRequest);

    StudySessionDetailsResponse getSessionDetails(String token, Long sessionId);

    StudySessionResultResponse completeStudySession(String token, Long sessionId);

    PageResponse<StudySessionResponse> getHistoryStudy(String token, int page, int size);

    StudySessionStatsResponse getStudySessionStats(String token, Integer collectionId);
}
