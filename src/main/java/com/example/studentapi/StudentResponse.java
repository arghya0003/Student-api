package com.example.studentapi;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private String name;
    private String email;
    private String course;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
