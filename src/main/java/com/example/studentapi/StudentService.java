package com.example.studentapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
            student.getId(),
            student.getName(),
            student.getEmail(),
            student.getCourse()
        );
    }

    private Student toEntity(StudentRequest request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());
        return student;
    }

    public Page<StudentResponse> getAllStudents(int page, int size) {
        return studentRepository.findAll(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    public StudentResponse getStudentById(Long id) {
        return toResponse(studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id)));
    }

    public StudentResponse createStudent(StudentRequest request) {
        return toResponse(studentRepository.save(toEntity(request)));
    }

    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());
        return toResponse(studentRepository.save(student));
    }

    public void deleteStudent(Long id) {
        studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.deleteById(id);
    }

    public List<StudentResponse> getStudentsByCourse(String course) {
        return studentRepository.findByCourse(course)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<StudentResponse> getStudentsByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
