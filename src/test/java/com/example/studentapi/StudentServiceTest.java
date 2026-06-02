package com.example.studentapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student sampleStudent() {
        Student s = new Student();
        s.setId(1L);
        s.setName("Arghya");
        s.setEmail("arghya@example.com");
        s.setCourse("Computer Science");
        return s;
    }

    private StudentRequest sampleRequest() {
        StudentRequest r = new StudentRequest();
        r.setName("Arghya");
        r.setEmail("arghya@example.com");
        r.setCourse("Computer Science");
        return r;
    }

    @Test
    void getAllStudents_returnsPage() {
        Page<Student> page = new PageImpl<>(List.of(sampleStudent()));
        when(studentRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<StudentResponse> result = studentService.getAllStudents(0, 10);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getStudentById_returnsStudent() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent()));

        StudentResponse result = studentService.getStudentById(1L);

        assertEquals("Arghya", result.getName());
    }

    @Test
    void getStudentById_throwsWhenNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentById(99L));
    }

    @Test
    void createStudent_savesAndReturns() {
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent());

        StudentResponse result = studentService.createStudent(sampleRequest());

        assertEquals("Arghya", result.getName());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void deleteStudent_throwsWhenNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.deleteStudent(99L));
    }
}
