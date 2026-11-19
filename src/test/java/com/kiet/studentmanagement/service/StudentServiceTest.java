package com.kiet.studentmanagement.service;

import com.kiet.studentmanagement.exception.DuplicateStudentIdException;
import com.kiet.studentmanagement.exception.InvalidStudentDataException;
import com.kiet.studentmanagement.exception.StudentNotFoundException;
import com.kiet.studentmanagement.model.Student;
import com.kiet.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration and Unit Tests for StudentService verifying all eight Q2 requirements.
 */
@SpringBootTest
@Transactional
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    public void cleanUp() {
        studentRepository.deleteAll();
    }

    // =========================================================================
    // REQUIREMENT 1: Calculate Average Score
    // =========================================================================
    @Test
    @DisplayName("Req 1: Calculate average score accurately without modifying original scores")
    public void testCalculateAverage() {
        Student student = new Student(201, "Aarav", "aarav@kiet.edu", "CSE", 85.0, 95.0, List.of("Java"));
        Double avg = studentService.calculateAverage(student);

        assertEquals(90.0, avg, 0.001);
        assertEquals(85.0, student.getProgrammingScore());
        assertEquals(95.0, student.getMathematicsScore());

        // Null student or scores check
        assertThrows(InvalidStudentDataException.class, () -> studentService.calculateAverage(null));
        student.setProgrammingScore(null);
        assertThrows(InvalidStudentDataException.class, () -> studentService.calculateAverage(student));
    }

    // =========================================================================
    // REQUIREMENT 2: Assign Performance Category
    // =========================================================================
    @Test
    @DisplayName("Req 2: Assign performance category with exact threshold boundaries")
    public void testGetPerformanceCategoryBoundaries() {
        Student student = new Student(202, "Diya", "diya@kiet.edu", "CSE", 0.0, 0.0, List.of("Java"));

        // 85.0 -> Excellent
        student.setProgrammingScore(85.0);
        student.setMathematicsScore(85.0);
        assertEquals("Excellent", studentService.getPerformanceCategory(student));

        // 84.99 -> Good (e.g. 84.98 and 85.0)
        student.setProgrammingScore(84.99);
        student.setMathematicsScore(84.99);
        assertEquals("Good", studentService.getPerformanceCategory(student));

        // 70.0 -> Good
        student.setProgrammingScore(70.0);
        student.setMathematicsScore(70.0);
        assertEquals("Good", studentService.getPerformanceCategory(student));

        // 69.99 -> Satisfactory
        student.setProgrammingScore(69.99);
        student.setMathematicsScore(69.99);
        assertEquals("Satisfactory", studentService.getPerformanceCategory(student));

        // 50.0 -> Satisfactory
        student.setProgrammingScore(50.0);
        student.setMathematicsScore(50.0);
        assertEquals("Satisfactory", studentService.getPerformanceCategory(student));

        // 49.99 -> Needs Improvement
        student.setProgrammingScore(49.99);
        student.setMathematicsScore(49.99);
        assertEquals("Needs Improvement", studentService.getPerformanceCategory(student));

        // 35.0 -> Needs Improvement
        student.setProgrammingScore(30.0);
        student.setMathematicsScore(40.0);
        assertEquals("Needs Improvement", studentService.getPerformanceCategory(student));
    }

    // =========================================================================
    // REQUIREMENT 3: Identify Stronger Subject
    // =========================================================================
    @Test
    @DisplayName("Req 3: Identify stronger subject (Programming, Mathematics, or Tie)")
    public void testGetStrongerSubject() {
        Student s1 = new Student(203, "Rohan", "rohan@kiet.edu", "CSE", 90.0, 75.0, List.of("Java"));
        assertEquals("Programming", studentService.getStrongerSubject(s1));

        Student s2 = new Student(204, "Priya", "priya@kiet.edu", "IT", 70.0, 88.0, List.of("Python"));
        assertEquals("Mathematics", studentService.getStrongerSubject(s2));

        Student s3 = new Student(205, "Kabir", "kabir@kiet.edu", "ECE", 80.0, 80.0, List.of("C"));
        assertEquals("Tie", studentService.getStrongerSubject(s3));
    }

    // =========================================================================
    // REQUIREMENT 4: Search Students by ID, Email, and Course
    // =========================================================================
    @Test
    @DisplayName("Req 4: Search students by ID, Email, and Course (case-insensitive)")
    public void testSearchStudents() {
        Student s1 = new Student(206, "Sameer", "sameer.c@kiet.edu", "Computer Science", 85.0, 90.0, List.of("Java"));
        Student s2 = new Student(207, "Neha", "neha.k@kiet.edu", "computer science", 80.0, 85.0, List.of("Python"));
        studentService.saveStudent(s1);
        studentService.saveStudent(s2);

        // Search by ID
        Student found = studentService.getStudentById(206);
        assertNotNull(found);
        assertEquals("Sameer", found.getStudentName());

        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentById(9999));

        // Search by Email (case-insensitive)
        List<Student> byEmail = studentService.searchByEmail("SAMEER.C@KIET.EDU");
        assertEquals(1, byEmail.size());
        assertEquals(206, byEmail.get(0).getStudentId());

        // Search by Course (case-insensitive)
        List<Student> byCourse = studentService.searchByCourse("COMPUTER SCIENCE");
        assertEquals(2, byCourse.size());
    }

    // =========================================================================
    // REQUIREMENT 5: Generate Merit List
    // =========================================================================
    @Test
    @DisplayName("Req 5: Generate merit list in descending order of average with tie-breaking by ID")
    public void testGetMeritList() {
        // Average: 95.0
        Student s1 = new Student(301, "Topper", "topper@kiet.edu", "CSE", 95.0, 95.0, List.of("Java"));
        // Average: 80.0, ID 302
        Student s2 = new Student(302, "Student B", "b@kiet.edu", "CSE", 80.0, 80.0, List.of("C++"));
        // Average: 80.0, ID 303 (tied average, secondary sort by ID ascending)
        Student s3 = new Student(303, "Student C", "c@kiet.edu", "CSE", 85.0, 75.0, List.of("SQL"));
        // Average: 60.0
        Student s4 = new Student(304, "Student D", "d@kiet.edu", "CSE", 60.0, 60.0, List.of("Python"));

        studentService.saveStudent(s3);
        studentService.saveStudent(s1);
        studentService.saveStudent(s4);
        studentService.saveStudent(s2);

        List<Student> meritList = studentService.getMeritList();
        assertEquals(4, meritList.size());
        assertEquals(301, meritList.get(0).getStudentId()); // 95.0
        assertEquals(302, meritList.get(1).getStudentId()); // 80.0 (ID 302 before 303)
        assertEquals(303, meritList.get(2).getStudentId()); // 80.0
        assertEquals(304, meritList.get(3).getStudentId()); // 60.0
    }

    // =========================================================================
    // REQUIREMENT 6: Group Students by Common Skills & Exact Skill Matches
    // =========================================================================
    @Test
    @DisplayName("Req 6: Identify students sharing common skills and exact matching skill sets")
    public void testSkillGroupings() {
        Student s1 = new Student(401, "Alice", "alice@kiet.edu", "CSE", 80.0, 80.0,
                Arrays.asList(" Java ", "Python", "SQL"));
        Student s2 = new Student(402, "Bob", "bob@kiet.edu", "IT", 75.0, 75.0,
                Arrays.asList("JAVA", "Spring Boot", "sql"));
        Student s3 = new Student(403, "Charlie", "charlie@kiet.edu", "CSE", 85.0, 85.0,
                Arrays.asList("python", "sql", "java"));
        Student s4 = new Student(404, "David", "david@kiet.edu", "ECE", 90.0, 90.0,
                List.of("Rust"));

        studentService.saveStudent(s1);
        studentService.saveStudent(s2);
        studentService.saveStudent(s3);
        studentService.saveStudent(s4);

        // Common skills grouping (minimum 2 students)
        Map<String, List<Student>> commonSkills = studentService.getStudentsGroupedByCommonSkills();

        // "java" is shared by Alice, Bob, Charlie (3 students)
        assertTrue(commonSkills.containsKey("java"));
        assertEquals(3, commonSkills.get("java").size());

        // "sql" is shared by Alice, Bob, Charlie (3 students)
        assertTrue(commonSkills.containsKey("sql"));
        assertEquals(3, commonSkills.get("sql").size());

        // "python" is shared by Alice and Charlie (2 students)
        assertTrue(commonSkills.containsKey("python"));
        assertEquals(2, commonSkills.get("python").size());

        // "spring boot" and "rust" only have 1 student each -> should NOT be included
        assertFalse(commonSkills.containsKey("spring boot"));
        assertFalse(commonSkills.containsKey("rust"));

        // Exact skill matches
        // Alice and Charlie both possess exactly {"java", "python", "sql"}
        Map<Set<String>, List<Student>> exactMatches = studentService.getStudentsWithExactMatchingSkills();
        assertEquals(1, exactMatches.size());
        Map.Entry<Set<String>, List<Student>> matchEntry = exactMatches.entrySet().iterator().next();
        assertEquals(Set.of("java", "python", "sql"), matchEntry.getKey());
        assertEquals(2, matchEntry.getValue().size());
    }

    // =========================================================================
    // REQUIREMENT 7: Programming Improvement Students (Prog < 50 AND Math >= 75)
    // =========================================================================
    @Test
    @DisplayName("Req 7: Filter programming improvement students with boundary tests")
    public void testProgrammingImprovementFilter() {
        // Included: prog 49.0, math 75.0
        Student sA = new Student(501, "Student A", "a@kiet.edu", "CSE", 49.0, 75.0, List.of("Java"));
        // Excluded: prog 50.0 (must be < 50), math 75.0
        Student sB = new Student(502, "Student B", "b@kiet.edu", "CSE", 50.0, 75.0, List.of("Java"));
        // Excluded: prog 40.0, math 74.0 (must be >= 75)
        Student sC = new Student(503, "Student C", "c@kiet.edu", "CSE", 40.0, 74.0, List.of("Java"));
        // Included: prog 20.0, math 90.0
        Student sD = new Student(504, "Student D", "d@kiet.edu", "CSE", 20.0, 90.0, List.of("Java"));

        studentService.saveStudent(sA);
        studentService.saveStudent(sB);
        studentService.saveStudent(sC);
        studentService.saveStudent(sD);

        List<Student> needingImprovement = studentService.getStudentsNeedingProgrammingImprovement();
        assertEquals(2, needingImprovement.size());
        assertTrue(needingImprovement.stream().anyMatch(s -> s.getStudentId().equals(501)));
        assertTrue(needingImprovement.stream().anyMatch(s -> s.getStudentId().equals(504)));
        assertFalse(needingImprovement.stream().anyMatch(s -> s.getStudentId().equals(502)));
        assertFalse(needingImprovement.stream().anyMatch(s -> s.getStudentId().equals(503)));
    }

    // =========================================================================
    // REQUIREMENT 8: Prevent Duplicate IDs and Validate Scores
    // =========================================================================
    @Test
    @DisplayName("Req 8: Prevent duplicate IDs and enforce score boundaries")
    public void testSaveValidationAndDuplicatePrevention() {
        Student validStudent = new Student(601, "Karan", "karan@kiet.edu", "IT", 85.0, 85.0, List.of("Java"));
        Student saved = studentService.saveStudent(validStudent);
        assertNotNull(saved);

        // Duplicate ID rejection
        Student duplicate = new Student(601, "Karan Copy", "other@kiet.edu", "IT", 90.0, 90.0, List.of("Python"));
        assertThrows(DuplicateStudentIdException.class, () -> studentService.saveStudent(duplicate));

        // Score < 0 rejection
        Student negativeProg = new Student(602, "Negative Prog", "neg@kiet.edu", "IT", -1.0, 80.0, List.of("Java"));
        assertThrows(InvalidStudentDataException.class, () -> studentService.saveStudent(negativeProg));

        Student negativeMath = new Student(603, "Negative Math", "negm@kiet.edu", "IT", 80.0, -0.5, List.of("Java"));
        assertThrows(InvalidStudentDataException.class, () -> studentService.saveStudent(negativeMath));

        // Score > 100 rejection
        Student excessProg = new Student(604, "Excess Prog", "exc@kiet.edu", "IT", 100.5, 80.0, List.of("Java"));
        assertThrows(InvalidStudentDataException.class, () -> studentService.saveStudent(excessProg));

        Student excessMath = new Student(605, "Excess Math", "excm@kiet.edu", "IT", 80.0, 101.0, List.of("Java"));
        assertThrows(InvalidStudentDataException.class, () -> studentService.saveStudent(excessMath));

        // Null score rejection
        Student nullProg = new Student(606, "Null Prog", "nullp@kiet.edu", "IT", null, 80.0, List.of("Java"));
        assertThrows(InvalidStudentDataException.class, () -> studentService.saveStudent(nullProg));
    }
}
