package com.authservice.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.authservice.dto.CourseDTO;
import com.authservice.models.Course;

public interface CourseService {

    public Course addCourse(CourseDTO req); 

    public Course searchByCourseName(String courseName);

    public Course searchByCourseId(String courseId);

    public Course updateCourseById(Long courseId, CourseDTO req);

    public Boolean deleteCourseById(Long id); 

    public Course getCourseById(Long courseId); 

    public Page<Course> getCoursesList(Pageable pageable); 

    public List<Course> getCoursesList();
    
}
