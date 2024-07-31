package com.authservice.controllers.master;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.dto.CourseDTO;
import com.authservice.models.Course;
import com.authservice.services.CourseService;

@RestController
@CrossOrigin
@RequestMapping("/masterservice/api/course")
public class CourseController {

    /**
     * * ===========================================================================
     * * ======================== Module : CourseController ========================
     * * ======================== Created By : Umesh Kumar =========================
     * * ======================== Created On : 04-06-2024 ==========================
     * * ===========================================================================
     * * | Code Status : On
     */
    
    @Autowired
    private CourseService courseService;


    /**
     * * Add a New Course
     * * API : http://localhost:8080/api/course/add
     */
    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@Validated @RequestBody CourseDTO req,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Construct error response
            Map<Object, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("error", "Validation Error");
            errorResponse.put("message",
                    "Validation failed for object 'categoryRequest'. Error count: " + bindingResult.getErrorCount());

            List<Map<String, String>> errors = new ArrayList<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                Map<String, String> error = new HashMap<>();
                error.put("field", fieldError.getField());
                error.put("message", fieldError.getDefaultMessage());
                errors.add(error);
            }
            errorResponse.put("errors", errors);

            return ResponseEntity.badRequest().body(errorResponse);
        }
        Course checkCourseIdExisting = courseService.searchByCourseId(req.getCourseId());
        if (checkCourseIdExisting != null)
            throw new IllegalStateException("Course Id already exists !!");

        Course checkCourseExisting = courseService.searchByCourseName(req.getCourseName());
        if (checkCourseExisting != null)
            throw new IllegalStateException("Course Name already exists !!");

        Map<Object, Object> resp = new HashMap<>();

        Course course = courseService.addCourse(req);

        resp.put("data", course);
        resp.put("message", "Course Added Successfully");
        resp.put("status", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }





    /**
     * * Get All Course Details
     * * API : http://localhost:8080/api/course/get-list
     * 
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("/get-list")
    public ResponseEntity<Map<Object, Object>> getCategoryList(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit) {

        Pageable pageable = PageRequest.of(offset, limit, Sort.by("id").descending());
        Page<Course> coursePage = courseService.getCoursesList(pageable);

        List<Course> categoryList = coursePage.getContent();

        Map<Object, Object> resp = new HashMap<>();
        resp.put("message", "Retrieve All Courses");
        resp.put("data", categoryList);
        resp.put("currentPage", coursePage.getNumber());
        resp.put("totalItems", coursePage.getTotalElements());
        resp.put("totalPages", coursePage.getTotalPages());
        resp.put("status", true);

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
