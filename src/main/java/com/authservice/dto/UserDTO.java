package com.authservice.dto;

import java.time.LocalDate;

import com.authservice.models.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    private Long id;

    private String fullname;

    private String email;

    private String password;

    private LocalDate dob;

    private Long phoneNo;

    private Role role;

    private String message;
}
