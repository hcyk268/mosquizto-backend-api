package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.AnswerRequest;
import com.mosquizto.api.dto.request.StartStudySessionRequest;
import com.mosquizto.api.dto.request.StudySessionDetailRequest;
import com.mosquizto.api.dto.response.*;
import com.mosquizto.api.model.StudySession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.List;

public interface StudySessionService {
    Long startStudySession(StartStudySessionRequest startStudySession, String idempotencyKey);

    AnswerResultResponse answerItems(Long sessionId, AnswerRequest answerRequest, String idempotencyKey);

    StudySessionDetailsResponse getSessionDetails(Long sessionId);

    StudySessionResultResponse completeStudySession(Long sessionId, String idempotencyKey);

    PageResponse<StudySessionResponse> getHistoryStudy(int page, int size);

    StudySessionStatsResponse getStudySessionStats(Integer collectionId);

    StudySessionResultResponse completeBatch(Long sessionId,List<StudySessionDetailRequest> detailRequests, boolean isFullTest);

    List<StudySessionResponse> getJumpBackInStudySession();

    void deleteStudySession(Long studySessionId);
}

