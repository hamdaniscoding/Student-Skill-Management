package com.kiet.studentmanagement.controller;

import com.kiet.studentmanagement.dto.StudentAssessmentResponse;
import com.kiet.studentmanagement.dto.StudentUpdateRequest;
import com.kiet.studentmanagement.model.Student;
import com.kiet.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for managing Student Profiles and Technical Skills.
 * Base Path: /api/students
 *
 * Implements:
 * 1. POST   /api/students               - Add a new student (201 Created)
 * 2. GET    /api/students               - Retrieve all students (200 OK)
 * 3. GET    /api/students/merit-list    - Display merit list ordered by average descending (200 OK)
 * 4. GET    /api/students/{id}          - Search student by primary key ID (200 OK or 404 Not Found)
 * 5. PUT    /api/students/{id}          - Update scores and technical skills only (200 OK)
 * 6. DELETE /api/students/{id}          - Delete student and cascade skills (204 No Content)
 *
 * Additional Endpoints:
 * 7. GET    /api/students/search/email  - Search students by email (case-insensitive)
 * 8. GET    /api/students/search/course - Search students by course (case-insensitive)
 * 9. GET    /api/students/{id}/assessment - Performance category and stronger subject analysis
 * 10. GET   /api/students/skills/common - Students grouped by common technical skills (>= 2 students)
 * 11. GET   /api/students/programming-improvement - Students needing programming improvement
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    /**
     * Constructor-based dependency injection.
     *
     * @param studentService The business logic service layer
     */
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // =========================================================================
    // API 1: Add a Student
    // POST /api/students
    // =========================================================================
    @PostMapping
    public ResponseEntity<Student> addStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.saveStudent(student);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    // =========================================================================
    // API 2: Retrieve All Students
    // GET /api/students
    // =========================================================================
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    // =========================================================================
    // API 6: Display Merit List
    // GET /api/students/merit-list
    // (Explicitly placed before /{id} to ensure unambiguous path matching)
    // =========================================================================
    @GetMapping("/merit-list")
    public ResponseEntity<List<Student>> getMeritList() {
        List<Student> meritList = studentService.getMeritList();
        return ResponseEntity.ok(meritList);
    }

    // =========================================================================
    // Additional API: Students Needing Programming Improvement
    // GET /api/students/programming-improvement
    // =========================================================================
    @GetMapping("/programming-improvement")
    public ResponseEntity<List<Student>> getStudentsNeedingProgrammingImprovement() {
        List<Student> improvementList = studentService.getStudentsNeedingProgrammingImprovement();
        return ResponseEntity.ok(improvementList);
    }

    // =========================================================================
    // Additional API: Students Grouped by Common Technical Skills
    // GET /api/students/skills/common
    // =========================================================================
    @GetMapping("/skills/common")
    public ResponseEntity<Map<String, List<Student>>> getCommonSkills() {
        Map<String, List<Student>> commonSkills = studentService.getStudentsGroupedByCommonSkills();
        return ResponseEntity.ok(commonSkills);
    }

    // =========================================================================
    // Additional API: Search by Email
    // GET /api/students/search/email?email=...
    // =========================================================================
    @GetMapping("/search/email")
    public ResponseEntity<List<Student>> searchByEmail(@RequestParam String email) {
        List<Student> students = studentService.searchByEmail(email);
        return ResponseEntity.ok(students);
    }

    // =========================================================================
    // Additional API: Search by Course
    // GET /api/students/search/course?course=...
    // =========================================================================
    @GetMapping("/search/course")
    public ResponseEntity<List<Student>> searchByCourse(@RequestParam String course) {
        List<Student> students = studentService.searchByCourse(course);
        return ResponseEntity.ok(students);
    }

    // =========================================================================
    // API 3: Search Student by ID
    // GET /api/students/{id}
    // =========================================================================
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    // =========================================================================
    // Additional API: Student Performance Assessment
    // GET /api/students/{id}/assessment
    // =========================================================================
    @GetMapping("/{id:[0-9]+}/assessment")
    public ResponseEntity<StudentAssessmentResponse> getStudentAssessment(@PathVariable Integer id) {
        StudentAssessmentResponse assessment = studentService.getStudentAssessment(id);
        return ResponseEntity.ok(assessment);
    }

    // =========================================================================
    // API 4: Update Student Scores and Skills
    // PUT /api/students/{id}
    // =========================================================================
    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<Student> updateStudentScoresAndSkills(
            @PathVariable Integer id,
            @Valid @RequestBody StudentUpdateRequest updateRequest) {

        Student updated = studentService.updateStudentScoresAndSkills(
                id,
                updateRequest.getProgrammingScore(),
                updateRequest.getMathematicsScore(),
                updateRequest.getTechnicalSkills()
        );
        return ResponseEntity.ok(updated);
    }

    // =========================================================================
    // API 5: Delete a Student
    // DELETE /api/students/{id}
    // =========================================================================
    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
