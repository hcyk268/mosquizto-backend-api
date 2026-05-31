package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CreateCourseRequest;
import com.mosquizto.api.dto.request.UpdateCourseRequest;
import com.mosquizto.api.dto.response.BestLearntCollectionResponse;
import com.mosquizto.api.dto.response.CollectionSummaryResponse;
import com.mosquizto.api.dto.response.CourseMemberResponse;
import com.mosquizto.api.dto.response.CourseResponse;
import com.mosquizto.api.dto.response.JoinResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.exception.AccessDeniedException;
import com.mosquizto.api.exception.BusinessRuleException;
import com.mosquizto.api.exception.ConflictException;
import com.mosquizto.api.exception.ErrorCode;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CourseMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.Course;
import com.mosquizto.api.model.CourseCollection;
import com.mosquizto.api.model.StudySession;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCourse;
import com.mosquizto.api.repository.CourseCollectionRepository;
import com.mosquizto.api.repository.CourseRepository;
import com.mosquizto.api.repository.StudySessionRepository;
import com.mosquizto.api.repository.UserCourseRepository;
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CourseService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.util.AccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {

    private final CurrentUserProvider currentUserProvider;
    private final CourseRepository courseRepository;
    private final CourseCollectionRepository courseCollectionRepository;
    private final UserCourseRepository userCourseRepository;
    private final StudySessionRepository studySessionRepository;
    private final CollectionService collectionService;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public Long createCourse(CreateCourseRequest createCourseRequest) {
        User user = this.currentUserProvider.getCurrentUser();

        Course course = Course.create(
                createCourseRequest.getTitle(),
                createCourseRequest.getDescription(),
                createCourseRequest.getVisibility(),
                createCourseRequest.getThumbnailUrl(),
                user
        );

        Course savedCourse = this.courseRepository.save(course);
        return savedCourse.getId();
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest updateCourseRequest) {
        User user = this.currentUserProvider.getCurrentUser();

        Course course = this.getCourseById(courseId);

        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        this.validateManager(course, currentUserCourse, "Only teacher can update this course");

        boolean hasUpdatedField = false;

        if (updateCourseRequest.getTitle() != null) {
            if (!StringUtils.hasText(updateCourseRequest.getTitle())) {
                throw new InvalidDataException("title must be not blank");
            }
            hasUpdatedField = true;
        }

        if (updateCourseRequest.getDescription() != null) {
            if (!StringUtils.hasText(updateCourseRequest.getDescription())) {
                throw new InvalidDataException("description must be not blank");
            }
            hasUpdatedField = true;
        }

        if (updateCourseRequest.getVisibility() != null) {
            hasUpdatedField = true;
        }

        if (updateCourseRequest.getThumbnailUrl() != null) {
            hasUpdatedField = true;
        }

        if (!hasUpdatedField) {
            throw new InvalidDataException("At least one field must be provided");
        }

        course.updateInfo(
                updateCourseRequest.getTitle(),
                updateCourseRequest.getDescription(),
                updateCourseRequest.getVisibility(),
                updateCourseRequest.getThumbnailUrl()
        );

        return this.courseMapper.toResponse(course, currentUserCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        User user = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        this.validateManager(course, currentUserCourse, "Only teacher can delete this course");

        this.courseRepository.delete(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseDetail(Long courseId) {
        User user = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        if (!course.canView(currentUserCourse)) {
            throw new AccessDeniedException("You do not have permission to view this course");
        }

        return this.courseMapper.toResponse(course, currentUserCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> getMyCourses(int page, int size) {
        User user = this.currentUserProvider.getCurrentUser();

        Page<UserCourse> userCoursePage = this.userCourseRepository.findActiveCoursesByUserIdAndStatus(
                user.getId(),
                AccessStatus.ENABLE,
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "course.createdAt")));

        List<CourseResponse> items = userCoursePage.getContent().stream()
                .map(userCourse -> this.courseMapper.toResponse(userCourse.getCourse(), userCourse))
                .toList();

        return this.toPageResponse(page, size, userCoursePage, items);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> getPublicCourses(int page, int size) {
        User user = this.currentUserProvider.getCurrentUser();

        Page<Course> coursePage = this.courseRepository.findPublicCourses(
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<CourseResponse> items = coursePage.getContent().stream()
                .map(course -> this.courseMapper.toResponse(
                        course,
                        this.getCurrentUserCourse(user.getId(), course.getId()).orElse(null)))
                .toList();

        return this.toPageResponse(page, size, coursePage, items);
    }

    @Override
    @Transactional
    public CollectionSummaryResponse addCollection(Long courseId, Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        this.validateManager(course, currentUserCourse, "Only teacher can add collection to this course");

        Collection collection = this.collectionService.getById(collectionId);
        boolean accessibility = this.collectionService.isAccessible(collectionId);

        if (!accessibility) {
            throw new AccessDeniedException("You might not access this collection");
        }

        int maxOrderIndex = this.courseCollectionRepository.findMaxActiveOrderIndex(courseId);
        CourseCollection courseCollection = course.addCollection(collection, maxOrderIndex + 1);

        this.courseCollectionRepository.save(courseCollection);

        return this.courseMapper.toCollectionSummaryResponse(courseCollection);
    }

    @Override
    @Transactional
    public void deleteCollection(Long courseId, Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        this.validateManager(course, currentUserCourse, "Only teacher can remove collection from this course");

        CourseCollection courseCollection = this.courseCollectionRepository.findActiveByCourseIdAndCollectionId(courseId, collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection does not exist in course"));

        this.courseCollectionRepository.delete(courseCollection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionSummaryResponse> getCollections(Long courseId) {
        User user = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        if (!course.canView(currentUserCourse)) {
            throw new AccessDeniedException("You do not have permission to view this course");
        }

        return this.getOrderedEnabledCollections(courseId);
    }

    @Override
    @Transactional
    public JoinResponse joinCourse(Long courseId) {
        User user = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse savedUserCourse = this.userCourseRepository.save(course.requestJoin(user));

        return this.courseMapper.toJoinResponse(savedUserCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JoinResponse> getPendingJoinRequests(Long courseId, int page, int size) {
        User user = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse userCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        this.validateManager(course, userCourse, "You can not access pending join list");

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<UserCourse> pendingJoins = this.userCourseRepository.findActiveMembersByCourseIdAndStatus(courseId, AccessStatus.PENDING, pageable);

        List<JoinResponse> joinResponses = pendingJoins.getContent().stream()
                .map(this.courseMapper::toJoinResponse)
                .toList();

        return this.toPageResponse(page, size, pendingJoins, joinResponses);
    }

    @Override
    @Transactional
    public void approveJoinRequest(Long courseId, Long userId) {
        User currentUser = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(currentUser.getId(), courseId).orElse(null);

        this.validateManager(course, currentUserCourse, "You can not approve join request");

        UserCourse joinRequest = this.userCourseRepository.findActiveByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found"));

        if (!joinRequest.isPending()) {
            throw new BusinessRuleException(ErrorCode.JOIN_REQUEST_NOT_PENDING, "Join request is not pending");
        }

        joinRequest.approve();
        this.userCourseRepository.save(joinRequest);
    }

    @Override
    @Transactional
    public void removeStudentFromCourse(Long courseId, Long userId) {
        User currentUser = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(currentUser.getId(), courseId).orElse(null);

        this.validateManager(course, currentUserCourse, "You can not remove student from course");

        UserCourse targetUserCourse = this.userCourseRepository.findActiveByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found in course"));

        if (targetUserCourse.isTeacher()) {
            throw new BusinessRuleException(ErrorCode.CANNOT_REMOVE_TEACHER, "You can not remove teacher from course");
        }

        if (!targetUserCourse.isStudent() || !targetUserCourse.isEnabled()) {
            throw new BusinessRuleException(ErrorCode.MEMBER_NOT_ACTIVE, "Student is not an active course member");
        }

        targetUserCourse.deny();
        this.userCourseRepository.save(targetUserCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseMemberResponse> getCourseMembers(Long courseId, int page, int size) {
        User currentUser = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(currentUser.getId(), courseId).orElse(null);

        this.validateManager(course, currentUserCourse, "You can not access course members");

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "role")
                .and(Sort.by(Sort.Direction.ASC, "user.username")));
        Page<UserCourse> memberPage = this.userCourseRepository.findActiveMembersByCourseIdAndStatus(courseId, AccessStatus.ENABLE, pageable);

        List<CourseMemberResponse> members = memberPage.getContent().stream()
                .map(this.courseMapper::toCourseMemberResponse)
                .toList();

        return this.toPageResponse(page, size, memberPage, members);
    }

    @Override
    @Transactional(readOnly = true)
    public BestLearntCollectionResponse getBestLearntCollections(Long courseId) {
        User currentUser = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(currentUser.getId(), courseId)
                .orElse(null);

        if (!course.canView(currentUserCourse) || currentUserCourse == null || !currentUserCourse.isEnabled()) {
            throw new AccessDeniedException("Only course members can access course stats");
        }

        List<StudySession> completedSessions = this.studySessionRepository.findCompletedCourseStudySessions(
                courseId,
                AccessStatus.ENABLE,
                AccessStatus.ENABLE);

        Map<Integer, List<StudySession>> sessionsByCollection = completedSessions.stream()
                .filter(session ->
                        session.getCollection() != null && session.getCollection().getId() != null)
                .collect(Collectors.groupingBy(session -> session.getCollection().getId()));

        return sessionsByCollection.values().stream()
                .map(this::toBestLearntCollectionResponse)
                .sorted(Comparator.comparing(
                                BestLearntCollectionResponse::getStudySessionCount,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(
                                BestLearntCollectionResponse::getCollectionName,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countStudySessionsInCourse(Long courseId) {
        User currentUser = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(currentUser.getId(), courseId)
                .orElse(null);

        if (!course.canView(currentUserCourse) || currentUserCourse == null || !currentUserCourse.isEnabled()) {
            throw new AccessDeniedException("Only course members can access course stats");
        }

        return this.studySessionRepository.countCompletedCourseStudySessions(
                courseId,
                AccessStatus.ENABLE,
                AccessStatus.ENABLE);
    }

    private Course getCourseById(Long courseId) {
        return this.courseRepository.findActiveById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private Optional<UserCourse> getCurrentUserCourse(Long userId, Long courseId) {
        return this.userCourseRepository.findActiveByUserIdAndCourseId(userId, courseId);
    }

    private void validateManager(Course course, UserCourse userCourse, String message) {
        if (!course.canManage(userCourse)) {
            throw new AccessDeniedException(message);
        }
    }

    private List<CollectionSummaryResponse> getOrderedEnabledCollections(Long courseId) {
        return this.courseCollectionRepository.findActiveByCourseIdAndStatus(courseId, AccessStatus.ENABLE)
                .stream()
                .map(this.courseMapper::toCollectionSummaryResponse)
                .toList();
    }

    private BestLearntCollectionResponse toBestLearntCollectionResponse(List<StudySession> sessions) {
        Collection collection = sessions.get(0).getCollection();

        return BestLearntCollectionResponse.builder()
                .collectionId(collection.getId())
                .collectionName(collection.getTitle())
                .studySessionCount((long) sessions.size())
                .build();
    }

    private <T> PageResponse<T> toPageResponse(int page,
                                               int size,
                                               Page<?> coursePage,
                                               List<T> items) {
        return PageResponse.<T>builder()
                .page(page)
                .size(size)
                .totalElements(coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .items(items)
                .build();
    }
}
