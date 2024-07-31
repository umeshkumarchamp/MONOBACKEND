package com.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDTO {

    @NotBlank(message = "Course Id is required")
    private String courseId;

    @NotBlank(message = "Course name is required")
    private String courseName; 

    @NotBlank(message = "Course duration is required")
    private String courseDuration; 

    @NotNull(message = "Course fee is required")
    private Double courseFee;
}
