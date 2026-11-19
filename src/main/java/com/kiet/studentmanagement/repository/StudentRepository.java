package com.kiet.studentmanagement.repository;

import com.kiet.studentmanagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository interface for Student persistence.
 *
 * Inherits standard CRUD operations:
 * - save(Student)
 * - findAll()
 * - findById(Integer)
 * - existsById(Integer)
 * - deleteById(Integer)
 *
 * Provides derived query methods for case-insensitive searching.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    /**
     * Finds students whose email matches the supplied email address, ignoring case.
     *
     * @param email The email address to look up
     * @return List of matching students
     */
    List<Student> findByEmailIgnoreCase(String email);

    /**
     * Finds students enrolled in a specific academic course, ignoring case.
     * Multiple students may share the same course.
     *
     * @param courseName The academic course name to look up
     * @return List of students enrolled in the course
     */
    List<Student> findByCourseNameIgnoreCase(String courseName);
}
