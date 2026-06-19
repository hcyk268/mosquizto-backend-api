package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.response.CollectionSummaryResponse;
import com.mosquizto.api.dto.response.CourseMemberResponse;
import com.mosquizto.api.dto.response.CourseResponse;
import com.mosquizto.api.dto.response.JoinResponse;
import com.mosquizto.api.model.Course;
import com.mosquizto.api.model.CourseCollection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCourse;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CourseRole;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseResponse toResponse(Course course, UserCourse currentUserCourse) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .visibility(course.getVisibility())
                .thumbnailUrl(course.getThumbnailUrl())
                .collectionCount(countEnabledCollections(course))
                .memberCount(countEnabledMembers(course))
                .currentUserRole(getCurrentUserRole(currentUserCourse))
                .build();
    }

    public CollectionSummaryResponse toCollectionSummaryResponse(CourseCollection courseCollection) {
        return CollectionSummaryResponse.builder()
                .id(courseCollection.getCollection().getId())
                .title(courseCollection.getCollection().getTitle())
                .orderIndex(courseCollection.getOrderIndex())
                .build();
    }

    public JoinResponse toJoinResponse(UserCourse userCourse) {
        Course course = userCourse.getCourse();

        return JoinResponse.builder()
                .id(userCourse.getId())
                .userId(userCourse.getUser().getId())
                .courseId(course.getId())
                .titleCourse(course.getTitle())
                .status(userCourse.getAccessStatus())
                .role(userCourse.getRole())
                .build();
    }

    public CourseMemberResponse toCourseMemberResponse(UserCourse userCourse) {
        User user = userCourse.getUser();

        return CourseMemberResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .imgUri(user.getAvatarUrl())
                .role(userCourse.getRole())
                .status(userCourse.getAccessStatus())
                .joinedAt(userCourse.getJoinedAt())
                .build();
    }

    private Integer countEnabledCollections(Course course) {
        if (course.getCourseCollections() == null) {
            return 0;
        }

        return (int) course.getCourseCollections().stream()
                .filter(CourseCollection::isEnabled)
                .count();
    }

    private Integer countEnabledMembers(Course course) {
        if (course.getUserCourses() == null) {
            return 0;
        }

        return (int) course.getUserCourses().stream()
                .filter(UserCourse::isEnabled)
                .count();
    }

    private CourseRole getCurrentUserRole(UserCourse currentUserCourse) {
        return currentUserCourse == null ? null : currentUserCourse.getRole();
    }
}
