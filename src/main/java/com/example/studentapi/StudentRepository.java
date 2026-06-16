package com.example.studentapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByCourse(String course);
    List<Student> findByNameContainingIgnoreCase(String name);
    Optional<Student> findByRollNumber(String rollNumber);
    long countByCourse(String course);

    @Query("SELECT s FROM Student s WHERE " +
           "(:query = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "             OR LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:course = '' OR s.course = :course) " +
           "ORDER BY s.id ASC")
    List<Student> filter(@Param("query") String query, @Param("course") String course);

    @Query("SELECT DISTINCT s.course FROM Student s ORDER BY s.course ASC")
    List<String> findAllDistinctCourses();
}
