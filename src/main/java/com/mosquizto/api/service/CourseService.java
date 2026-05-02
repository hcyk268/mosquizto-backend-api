package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.CreateCourseRequest;
import com.mosquizto.api.dto.request.UpdateCourseRequest;
import com.mosquizto.api.dto.response.CollectionSummaryResponse;
import com.mosquizto.api.dto.response.CourseResponse;
import com.mosquizto.api.dto.response.PageResponse;

import java.util.List;

public interface CourseService {

    Long createCourse(CreateCourseRequest createCourseRequest);

    CourseResponse updateCourse(Long courseId, UpdateCourseRequest updateCourseRequest);

    void deleteCourse(Long courseId);

    CourseResponse getCourseDetail(Long courseId);

    PageResponse<CourseResponse> getMyCourses(int page, int size);

    PageResponse<CourseResponse> getPublicCourses(int page, int size);

    CollectionSummaryResponse addCollection(Long courseId, Integer collectionId);

    void deleteCollection(Long courseId, Integer collectionId);

    List<CollectionSummaryResponse> getCollections(Long courseId);
}
