package com.mosquizto.api.controller.study;

import com.mosquizto.api.dto.request.AnswerRequest;
import com.mosquizto.api.dto.request.StartStudySessionRequest;
import com.mosquizto.api.dto.request.StudySessionDetailRequest;
import com.mosquizto.api.dto.response.*;
import com.mosquizto.api.service.StudySessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "Study Session", description = "Study flow, history and stats APIs")
public class StudySessionsController {

    private final StudySessionService studySessionService;

    @Operation(summary = "Start study session", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/study-sessions")
    public ResponseData<Long> startStudySession(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody StartStudySessionRequest startStudySession) {
        return new ResponseData<>(HttpStatus.OK.value(), "Start Successfully",
                this.studySessionService.startStudySession(startStudySession, idempotencyKey));
    }

    @Operation(summary = "Answer session item", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/study-sessions/{sessionId}/answers")
    public ResponseData<AnswerResultResponse> answerItems(@PathVariable Long sessionId,
                                                          @RequestHeader("Idempotency-Key") String idempotencyKey,
                                                          @Valid @RequestBody AnswerRequest answerRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Sent Answer Successfully",
                this.studySessionService.answerItems(sessionId, answerRequest, idempotencyKey));
    }

    @Operation(summary = "Complete study session", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/study-sessions/{sessionId}/complete")
    public ResponseData<StudySessionResultResponse> completeStudySession(@PathVariable Long sessionId,
                                                                         @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseData<>(HttpStatus.OK.value(), "Session completed successfully",
                this.studySessionService.completeStudySession(sessionId, idempotencyKey));
    }

    @Operation(summary = "Get session detail", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/study-sessions/{sessionId}")
    public ResponseData<StudySessionDetailsResponse> getSessionDetails(@PathVariable Long sessionId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get session details successfully",
                this.studySessionService.getSessionDetails(sessionId));
    }

    @Operation(summary = "Get study history", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/me/study-sessions")
    public ResponseData<PageResponse<StudySessionResponse>> getHistoryStudy(
            @Min(1) @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @Min(5) @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get study history successfully",
                this.studySessionService.getHistoryStudy(page, size));
    }

    @Operation(summary = "Get collection study stats", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/collections/{collectionId}/study-stats/me")
    public ResponseData<StudySessionStatsResponse> getStudySessionDetails(@PathVariable Integer collectionId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get study session stats successfully",
                this.studySessionService.getStudySessionStats(collectionId));
    }

    @Operation(summary = "Complete session batch", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/study-sessions/{sessionId}/answers/batch")
    public ResponseData<StudySessionResultResponse> completeBatch(
            @PathVariable("sessionId") Long sessionId , @RequestBody
             List<StudySessionDetailRequest> list ,
            @RequestParam(defaultValue = "false",value = "isFullTest", required = true) Boolean isFullTest)
    {
        return new ResponseData<>(HttpStatus.OK.value(), "Complete batch of session",
                this.studySessionService.completeBatch(sessionId,list,isFullTest));
    }

    @Operation(summary = "Get jump back in sessions", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/me/study-sessions/resumable")
    public ResponseData<List<StudySessionResponse>> getJumpBackIn()
    {
        return  new ResponseData<>(HttpStatus.OK.value(),"get jump back in study session",
                this.studySessionService.getJumpBackInStudySession());
    }

    @DeleteMapping("/study-sessions/{studySessionId}")
    public ResponseData<Void> deleteStudySession(@Valid @Positive @PathVariable Long studySessionId) {

        this.studySessionService.deleteStudySession(studySessionId);
        return new ResponseData<>(HttpStatus.OK.value(), "Delete this session successfully");
    }
}
