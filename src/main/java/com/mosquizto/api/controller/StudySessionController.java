package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.AnswerRequest;
import com.mosquizto.api.dto.request.StartStudySessionRequest;
import com.mosquizto.api.dto.response.*;
import com.mosquizto.api.service.StudySessionService;
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
    public ResponseData<Long> startStudySession(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody StartStudySessionRequest startStudySession) {
        return new ResponseData<>(HttpStatus.OK.value(), "Start Successfully",
                this.studySessionService.startStudySession(startStudySession, idempotencyKey));
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseData<AnswerResultResponse> answerItems(@PathVariable Long sessionId,
                                                          @RequestHeader("Idempotency-Key") String idempotencyKey,
                                                          @Valid @RequestBody AnswerRequest answerRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Sent Answer Successfully",
                this.studySessionService.answerItems(sessionId, answerRequest, idempotencyKey));
    }

    @PostMapping("/{sessionId}/complete")
    public ResponseData<StudySessionResultResponse> completeStudySession(@PathVariable Long sessionId,
                                                                         @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseData<>(HttpStatus.OK.value(), "Session completed successfully",
                this.studySessionService.completeStudySession(sessionId, idempotencyKey));
    }

    @GetMapping("/{sessionId}")
    public ResponseData<StudySessionDetailsResponse> getSessionDetails(@PathVariable Long sessionId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get session details successfully",
                this.studySessionService.getSessionDetails(sessionId));
    }

    @GetMapping("/history")
    public ResponseData<PageResponse<StudySessionResponse>> getHistoryStudy(
            @Min(1) @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @Min(5) @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get study history successfully",
                this.studySessionService.getHistoryStudy(page, size));
    }

    @GetMapping("/stats/{collectionId}")
    public ResponseData<StudySessionStatsResponse> getStudySessionDetails(@PathVariable Integer collectionId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get study session stats successfully",
                this.studySessionService.getStudySessionStats(collectionId));
    }
}
