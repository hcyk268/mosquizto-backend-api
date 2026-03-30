package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.AnswerRequest;
import com.mosquizto.api.dto.request.StartStudySessionRequest;
import com.mosquizto.api.dto.response.*;
import jakarta.validation.Valid;

public interface StudySessionService {
    Long startStudySession(StartStudySessionRequest startStudySession);

    AnswerResultResponse answerItems(Long sessionId, @Valid AnswerRequest answerRequest);

    StudySessionDetailsResponse getSessionDetails(Long sessionId);

    StudySessionResultResponse completeStudySession(Long sessionId);

    PageResponse<StudySessionResponse> getHistoryStudy(int page, int size);

    StudySessionStatsResponse getStudySessionStats(Integer collectionId);
}
