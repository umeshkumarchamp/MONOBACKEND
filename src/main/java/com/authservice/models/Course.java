package com.authservice.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "courses")
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(name = "course_id", nullable = false, unique = true)
    private String courseId;

    @Column(name = "course_name", nullable = false, unique = true)
    private String courseName; 

    @Column(nullable = false)
    private String duration; 

    @Column(nullable = false)
    private Double fee;
    private Boolean status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt; 

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
