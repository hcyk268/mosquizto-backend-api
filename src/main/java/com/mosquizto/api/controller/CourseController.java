package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.CreateCourseRequest;
import com.mosquizto.api.dto.request.UpdateCourseRequest;
import com.mosquizto.api.dto.response.*;
import com.mosquizto.api.service.CourseService;
import com.mosquizto.api.util.AccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/course")
@Tag(name = "Course", description = "APIs for managing courses")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Create new course", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("")
    public ResponseData<Long> createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Create course successfully",
                this.courseService.createCourse(createCourseRequest));
    }

    @Operation(summary = "Update course", security = @SecurityRequirement(name = "bearerAuth"))
    @RequestMapping(value = "/{courseId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseData<CourseResponse> updateCourse(@PathVariable Long courseId,
                                                     @Valid @RequestBody UpdateCourseRequest updateCourseRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Update course successfully",
                this.courseService.updateCourse(courseId, updateCourseRequest));
    }

    @Operation(summary = "Delete course", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{courseId}")
    public ResponseData<Void> deleteCourse(@PathVariable Long courseId) {
        this.courseService.deleteCourse(courseId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete course successfully");
    }

    @Operation(summary = "Get course detail", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{courseId}")
    public ResponseData<CourseResponse> getCourseDetail(@PathVariable Long courseId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get course successfully",
                this.courseService.getCourseDetail(courseId));
    }

    @Operation(summary = "Get my courses", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/my-list")
    public ResponseData<PageResponse<CourseResponse>> getMyCourses(
            @Min(1) @RequestParam(defaultValue = "1", name = "page") int page,
            @Min(1) @RequestParam(defaultValue = "10", name = "size") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get my courses successfully",
                this.courseService.getMyCourses(page, size));
    }

    @Operation(summary = "Get public courses", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/public")
    public ResponseData<PageResponse<CourseResponse>> getPublicCourses(
            @Min(1) @RequestParam(defaultValue = "1", name = "page") int page,
            @Min(1) @RequestParam(defaultValue = "10", name = "size") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get public courses successfully",
                this.courseService.getPublicCourses(page, size));
    }

    @Operation(summary = "Add collection to course", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{courseId}/collection/{collectionId}")
    public ResponseData<CollectionSummaryResponse> addCollection(@PathVariable Long courseId,
                                                                       @PathVariable Integer collectionId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Add collection successfully",
                this.courseService.addCollection(courseId, collectionId));
    }

    @Operation(summary = "Remove collection from course", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{courseId}/collection/{collectionId}")
    public ResponseData<Void> deleteCollection(@PathVariable Long courseId,
                                               @PathVariable Integer collectionId) {
        this.courseService.deleteCollection(courseId, collectionId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete collection successfully");
    }

    @Operation(summary = "Get course collections in order", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{courseId}/collection")
    public ResponseData<List<CollectionSummaryResponse>> getCollections(@PathVariable Long courseId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get course collections successfully",
                this.courseService.getCollections(courseId));
    }

    @Operation(summary = "Student join course", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{courseId}/join")
    public ResponseData<JoinResponse> joinCourse(@PathVariable Long courseId) {
        JoinResponse joinResponse = this.courseService.joinCourse(courseId);
        String message = AccessStatus.PENDING.equals(joinResponse.getStatus()) ? "Join request sent" : "Join successfully";

        return new ResponseData<>(HttpStatus.OK.value(), message, joinResponse);
    }

    @Operation(summary = "List pending join requests", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{courseId}/join-requests/pending")
    public ResponseData<PageResponse<JoinResponse>> getPendingJoinRequests(
            @PathVariable Long courseId,
            @Min(1) @RequestParam(defaultValue = "1", name = "page") int page,
            @Min(1) @RequestParam(defaultValue = "10", name = "size") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get list pending join successfully",
                this.courseService.getPendingJoinRequests(courseId, page, size));
    }

    @Operation(summary = "Approve join request", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{courseId}/join-requests/{userId}/approve")
    public ResponseData<Void> approveJoinRequest(@PathVariable Long courseId,
                                                 @PathVariable Long userId) {
        this.courseService.approveJoinRequest(courseId, userId);
        return new ResponseData<>(HttpStatus.OK.value(), "Approve join request successfully");
    }

    @Operation(summary = "Remove student from course", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{courseId}/members/{userId}")
    public ResponseData<Void> removeStudentFromCourse(@PathVariable Long courseId,
                                                      @PathVariable Long userId) {
        this.courseService.removeStudentFromCourse(courseId, userId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Remove student from course successfully");
    }

    @Operation(summary = "Get all course members", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{courseId}/members")
    public ResponseData<PageResponse<CourseMemberResponse>> getCourseMembers(
            @PathVariable Long courseId,
            @Min(1) @RequestParam(defaultValue = "1", name = "page") int page,
            @Min(1) @RequestParam(defaultValue = "10", name = "size") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get course members successfully",
                this.courseService.getCourseMembers(courseId, page, size));
    }

    @Operation(summary = "Get best learnt collections in course", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{courseId}/stats/best-collections-learnt")
    public ResponseData<BestLearntCollectionResponse> getBestLearntCollections(@PathVariable Long courseId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get best learnt collection successfully",
                this.courseService.getBestLearntCollections(courseId));
    }

    @Operation(summary = "Count study sessions in course", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{courseId}/stats/study-session-count")
    public ResponseData<Long> countStudySessionsInCourse(@PathVariable Long courseId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Count study sessions in course successfully",
                this.courseService.countStudySessionsInCourse(courseId));
    }
}
