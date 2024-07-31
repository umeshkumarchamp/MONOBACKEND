package com.authservice.services.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.authservice.dto.CourseDTO;
import com.authservice.models.Course;
import com.authservice.repository.CourseRepository;
import com.authservice.services.CourseService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService{

    @Autowired
    private CourseRepository courseRepo; 

    @Override
    public Course addCourse(CourseDTO req) {
        Course course = Course.builder()
                .courseName(req.getCourseName())
                .duration(req.getCourseDuration())
                .fee(req.getCourseFee())
                .status(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .courseId(req.getCourseId())
                .build();

        courseRepo.save(course);
        return course;
    }

    @Override
    public Course searchByCourseName(String courseName) {
        return courseRepo.findByCourseNameIgnoreCase(courseName);
    }

    @Override
    public Course searchByCourseId(String courseId) {
        return courseRepo.findByCourseIdIgnoreCase(courseId);
    }

    @Override
    public Course updateCourseById(Long courseId, CourseDTO req) {
        Course course = null;
        Optional<Course> op = courseRepo.findById(courseId);
        if (op.isPresent()) {
            course = op.get();
            course.setCourseId(req.getCourseId());
            course.setCourseName(req.getCourseName());
            course.setDuration(req.getCourseDuration());
            course.setFee(req.getCourseFee());
            course.setUpdatedAt(LocalDateTime.now());

            courseRepo.save(course);
        }
        return course;
    }

    @Override
    public Boolean deleteCourseById(Long id) {
        Optional<Course> op = courseRepo.findById(id);

        if (op.isPresent()) {
            Course course = op.get();
            course.setStatus(!course.getStatus());
            courseRepo.save(course);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Course getCourseById(Long courseId) {
        Optional<Course> op = courseRepo.findById(courseId);
        if(op.isPresent()) {
            return op.get();
        }   
        return null;
    }

    @Override
    public Page<Course> getCoursesList(Pageable pageable) {
        return courseRepo.findAll(pageable);
    }

    @Override
    public List<Course> getCoursesList() {
        // Creating a Sort object to specify the sorting criteria
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
    
        // Returning the list of courses sorted by descending order of ID
        return courseRepo.findAll(sort);
    }
    
}
