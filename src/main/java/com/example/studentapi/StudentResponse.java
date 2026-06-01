package com.example.studentapi;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private String name;
    private String email;
    private String course;
}
