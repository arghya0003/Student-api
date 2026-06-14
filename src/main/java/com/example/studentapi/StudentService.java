package com.example.studentapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
            student.getId(),
            student.getName(),
            student.getEmail(),
            student.getCourse(),
            student.getCreatedAt(),
            student.getUpdatedAt()
        );
    }

    private Student toEntity(StudentRequest request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());
        return student;
    }

    public Page<StudentResponse> getAllStudents(int page, int size, String sortBy, String direction) {
        log.info("Fetching students - page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return studentRepository.findAll(PageRequest.of(page, size, sort))
                .map(this::toResponse);
    }

    public StudentResponse getStudentById(Long id) {
        log.info("Fetching student with id: {}", id);
        return toResponse(studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id)));
    }

    public StudentResponse createStudent(StudentRequest request) {
        log.info("Creating student with email: {}", request.getEmail());
        return toResponse(studentRepository.save(toEntity(request)));
    }

    public StudentResponse updateStudent(Long id, StudentRequest request) {
        log.info("Updating student with id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());
        return toResponse(studentRepository.save(student));
    }

    public void deleteStudent(Long id) {
        log.info("Deleting student with id: {}", id);
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
