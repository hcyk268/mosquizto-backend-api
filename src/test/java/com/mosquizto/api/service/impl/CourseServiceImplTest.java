package com.mosquizto.api.service.impl;

import com.mosquizto.api.mapper.CourseMapper;
import com.mosquizto.api.model.Course;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CourseCollectionRepository;
import com.mosquizto.api.repository.CourseRepository;
import com.mosquizto.api.repository.StudySessionRepository;
import com.mosquizto.api.repository.UserCourseRepository;
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseCollectionRepository courseCollectionRepository;

    @Mock
    private UserCourseRepository userCourseRepository;

    @Mock
    private StudySessionRepository studySessionRepository;

    @Mock
    private CollectionService collectionService;

    @Mock
    private CourseMapper courseMapper;

    @Test
    void shouldSoftDeleteCourseInsteadOfHardDeleting() {
        CourseServiceImpl service = new CourseServiceImpl(
                this.currentUserProvider,
                this.courseRepository,
                this.courseCollectionRepository,
                this.userCourseRepository,
                this.studySessionRepository,
                this.collectionService,
                this.courseMapper
        );

        User user = new User();
        user.setId(1L);

        Course course = Course.create("Java", "Backend basics", true, null, user);
        course.setId(10L);

        when(this.currentUserProvider.getCurrentUser()).thenReturn(user);
        when(this.courseRepository.findActiveById(10L)).thenReturn(Optional.of(course));
        when(this.userCourseRepository.findActiveByUserIdAndCourseId(1L, 10L))
                .thenReturn(course.getUserCourses().stream().findFirst());

        service.deleteCourse(10L);

        assertNotNull(course.getDeletedAt());
        assertSame(user, course.getDeletedBy());
        verify(this.courseRepository, never()).delete(course);
    }
}
