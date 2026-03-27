package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.AnswerRequest;
import com.mosquizto.api.dto.request.StartStudySessionRequest;
import com.mosquizto.api.dto.response.*;
import com.mosquizto.api.service.StudySessionService;
import com.mosquizto.api.util.AuthorizationHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/study-session")
public class StudySessionController {

    private final StudySessionService studySessionService;

    @PostMapping("/start")
    public ResponseData<Long> startStudySession(HttpServletRequest request,
                                                  @Valid @RequestBody StartStudySessionRequest startStudySession) {
        return new ResponseData<>(HttpStatus.OK.value(), "Start Successfully",
                this.studySessionService.startStudySession(startStudySession, request));
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseData<AnswerResultResponse> answerItems(@PathVariable Long sessionId, @Valid @RequestBody AnswerRequest answerRequest, HttpServletRequest request) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Sent Answer Successfully", this.studySessionService.answerItems(token, sessionId, answerRequest));
    }

    @PostMapping("/{sessionId}/complete")
    public ResponseData<StudySessionResultResponse> completeStudySession(HttpServletRequest request, @PathVariable Long sessionId) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Session completed successfully",
                this.studySessionService.completeStudySession(token, sessionId));
    }

    @GetMapping("/{sessionId}")
    public ResponseData<StudySessionDetailsResponse> getSessionDetails(HttpServletRequest request,
                                                                       @PathVariable Long sessionId) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Get session details successfully",
                this.studySessionService.getSessionDetails(token, sessionId));
    }

    @GetMapping("/history")
    public ResponseData<PageResponse<StudySessionResponse>> getHistoryStudy(
            @Min(1) @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @Min(5) @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            HttpServletRequest request) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Get study history successfully", this.studySessionService.getHistoryStudy(token, page, size));
    }

    @GetMapping("/stats/{collectionId}")
    public ResponseData<StudySessionStatsResponse> getStudySessionDetails(HttpServletRequest request,
                                                                          @PathVariable Integer collectionId) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Get study session stats successfully",
                this.studySessionService.getStudySessionStats(token, collectionId));
    }
}
