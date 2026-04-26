package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.AnswerRequest;
import com.mosquizto.api.dto.request.StartStudySessionRequest;
import com.mosquizto.api.dto.request.StudySessionDetailRequest;
import com.mosquizto.api.dto.response.*;
import com.mosquizto.api.model.StudySession;
import jakarta.validation.Valid;

import java.util.List;

public interface StudySessionService {
    Long startStudySession(StartStudySessionRequest startStudySession);

    AnswerResultResponse answerItems(Long sessionId, @Valid AnswerRequest answerRequest);

    StudySessionDetailsResponse getSessionDetails(Long sessionId);

    StudySessionResultResponse completeStudySession(Long sessionId);

    PageResponse<StudySessionResponse> getHistoryStudy(int page, int size);

    StudySessionStatsResponse getStudySessionStats(Integer collectionId);

    StudySessionResultResponse completeBatch(Long sessionId,List<StudySessionDetailRequest> detailRequests);

    List<StudySessionResponse> getJumpBackInStudySession();
}

