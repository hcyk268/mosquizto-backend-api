package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CreateCourseRequest;
import com.mosquizto.api.dto.request.UpdateCourseRequest;
import com.mosquizto.api.dto.response.CollectionSummaryResponse;
import com.mosquizto.api.dto.response.CourseResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CourseMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.Course;
import com.mosquizto.api.model.CourseCollection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCourse;
import com.mosquizto.api.repository.CourseCollectionRepository;
import com.mosquizto.api.repository.CourseRepository;
import com.mosquizto.api.repository.UserCourseRepository;
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CourseService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CourseRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {

    private final CurrentUserProvider currentUserProvider;
    private final CourseRepository courseRepository;
    private final CourseCollectionRepository courseCollectionRepository;
    private final UserCourseRepository userCourseRepository;
    private final CollectionService collectionService;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public Long createCourse(CreateCourseRequest createCourseRequest) {
        User user = this.currentUserProvider.getCurrentUser();

        Course course = Course.builder()
                .title(createCourseRequest.getTitle())
                .description(createCourseRequest.getDescription())
                .visibility(createCourseRequest.getVisibility())
                .thumbnailUrl(createCourseRequest.getThumbnailUrl())
                .build();

        course.addMember(user, CourseRole.TEACHER, AccessStatus.ENABLE);

        Course savedCourse = this.courseRepository.save(course);
        return savedCourse.getId();
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest updateCourseRequest) {
        User user = this.currentUserProvider.getCurrentUser();

        Course course = this.getCourseById(courseId);

        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        this.validateTeacher(currentUserCourse, "Only teacher can update this course");

        boolean hasUpdatedField = false;

        if (updateCourseRequest.getTitle() != null) {
            if (!StringUtils.hasText(updateCourseRequest.getTitle())) {
                throw new InvalidDataException("title must be not blank");
            }
            course.setTitle(updateCourseRequest.getTitle());
            hasUpdatedField = true;
        }

        if (updateCourseRequest.getDescription() != null) {
            if (!StringUtils.hasText(updateCourseRequest.getDescription())) {
                throw new InvalidDataException("description must be not blank");
            }
            course.setDescription(updateCourseRequest.getDescription());
            hasUpdatedField = true;
        }

        if (updateCourseRequest.getVisibility() != null) {
            course.setVisibility(updateCourseRequest.getVisibility());
            hasUpdatedField = true;
        }

        if (updateCourseRequest.getThumbnailUrl() != null) {
            course.setThumbnailUrl(updateCourseRequest.getThumbnailUrl());
            hasUpdatedField = true;
        }

        if (!hasUpdatedField) {
            throw new InvalidDataException("At least one field must be provided");
        }

        return this.courseMapper.toResponse(course, currentUserCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        User user = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        this.validateTeacher(currentUserCourse, "Only teacher can delete this course");

        this.courseRepository.delete(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseDetail(Long courseId) {
        User user = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        if (!course.isVisible() && !isEnabledMember(currentUserCourse)) {
            throw new InvalidDataException("You do not have permission to view this course");
        }

        return this.courseMapper.toResponse(course, currentUserCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> getMyCourses(int page, int size) {
        User user = this.currentUserProvider.getCurrentUser();

        Page<UserCourse> userCoursePage = this.userCourseRepository.findAllByUserIdAndAccessStatusWithCourse(
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

        this.validateTeacher(currentUserCourse, "Only teacher can add collection to this course");

        Collection collection = this.collectionService.getById(collectionId);
        boolean accessibility = this.collectionService.isAccessible(collectionId);

        if (!accessibility) {
            throw new InvalidDataException("You might not access this collection");
        }

        if (this.courseCollectionRepository.existsByCourseIdAndCollectionId(courseId, collectionId)) {
            throw new InvalidDataException("Collection is already exists in course");
        }

        int maxOrderIndex = this.courseCollectionRepository.findMaxOrderIndexCollection(courseId);
        CourseCollection courseCollection = CourseCollection.create(course, collection, maxOrderIndex + 1);

        this.courseCollectionRepository.save(courseCollection);

        return this.courseMapper.toCollectionSummaryResponse(courseCollection);
    }

    @Override
    @Transactional
    public void deleteCollection(Long courseId, Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();
        this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        this.validateTeacher(currentUserCourse, "Only teacher can remove collection from this course");

        CourseCollection courseCollection = this.courseCollectionRepository.findByCourseIdAndCollectionId(courseId, collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection does not exist in course"));

        this.courseCollectionRepository.delete(courseCollection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionSummaryResponse> getCollections(Long courseId) {
        User user = this.currentUserProvider.getCurrentUser();
        Course course = this.getCourseById(courseId);
        UserCourse currentUserCourse = this.getCurrentUserCourse(user.getId(), courseId).orElse(null);

        if (!course.isVisible() && !isEnabledMember(currentUserCourse)) {
            throw new InvalidDataException("You do not have permission to view this course");
        }

        return this.getOrderedEnabledCollections(courseId);
    }

    private Course getCourseById(Long courseId) {
        return this.courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private Optional<UserCourse> getCurrentUserCourse(Long userId, Long courseId) {
        return this.userCourseRepository.findByUserIdAndCourseId(userId, courseId);
    }

    private void validateTeacher(UserCourse userCourse, String message) {
        if (userCourse == null || !userCourse.isEnabled() || !userCourse.isTeacher()) {
            throw new InvalidDataException(message);
        }
    }

    private boolean isEnabledMember(UserCourse userCourse) {
        return userCourse != null && userCourse.isEnabled();
    }

    private List<CollectionSummaryResponse> getOrderedEnabledCollections(Long courseId) {
        return this.courseCollectionRepository.findAllByCourseIdAndAccessStatusOrderByOrderIndex(courseId, AccessStatus.ENABLE)
                .stream()
                .map(this.courseMapper::toCollectionSummaryResponse)
                .toList();
    }

    private PageResponse<CourseResponse> toPageResponse(int page,
                                                        int size,
                                                        Page<?> coursePage,
                                                        List<CourseResponse> items) {
        return PageResponse.<CourseResponse>builder()
                .page(page)
                .size(size)
                .totalElements(coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .items(items)
                .build();
    }
}
