package com.kiet.studentmanagement.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for Student class verifying:
 * 1. Encapsulation (Getters and Setters)
 * 2. Constructors (Default and Parameterized)
 * 3. Collection requirement (List of Technical Skills)
 * 4. Object methods (toString, equals, hashCode handling null safely)
 * 5. Jakarta Validation constraints
 */
public class StudentTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should initialize student using default constructor and populate via setters")
    public void testDefaultConstructorAndEncapsulation() {
        Student student = new Student();
        student.setStudentId(101);
        student.setStudentName("Hamdan Khan");
        student.setEmail("hamdan@kiet.edu");
        student.setCourseName("B.Tech Computer Science");
        student.setProgrammingScore(95.5);
        student.setMathematicsScore(92.0);

        List<String> skills = new ArrayList<>(Arrays.asList("Java", "Spring Boot", "MySQL"));
        student.setTechnicalSkills(skills);

        assertEquals(101, student.getStudentId());
        assertEquals("Hamdan Khan", student.getStudentName());
        assertEquals("hamdan@kiet.edu", student.getEmail());
        assertEquals("B.Tech Computer Science", student.getCourseName());
        assertEquals(95.5, student.getProgrammingScore());
        assertEquals(92.0, student.getMathematicsScore());
        assertEquals(3, student.getTechnicalSkills().size());
        assertTrue(student.getTechnicalSkills().contains("Java"));
    }

    @Test
    @DisplayName("Should initialize student using parameterized constructor")
    public void testParameterizedConstructor() {
        List<String> skills = Arrays.asList("Java", "Python", "SQL", "Machine Learning");
        Student student = new Student(
                102,
                "Aman Sharma",
                "aman.sharma@kiet.edu",
                "Information Technology",
                88.0,
                84.5,
                skills
        );

        assertEquals(102, student.getStudentId());
        assertEquals("Aman Sharma", student.getStudentName());
        assertEquals("aman.sharma@kiet.edu", student.getEmail());
        assertEquals("Information Technology", student.getCourseName());
        assertEquals(88.0, student.getProgrammingScore());
        assertEquals(84.5, student.getMathematicsScore());
        assertEquals(4, student.getTechnicalSkills().size());
        assertEquals(skills, student.getTechnicalSkills());
    }

    @Test
    @DisplayName("Should correctly support multiple skills in List<String> and helper method")
    public void testTechnicalSkillsCollection() {
        Student student = new Student();
        student.addTechnicalSkill("Java");
        student.addTechnicalSkill("Python");
        student.addTechnicalSkill("SQL");
        student.addTechnicalSkill("Machine Learning");

        assertEquals(4, student.getTechnicalSkills().size());
        assertEquals("Java", student.getTechnicalSkills().get(0));
        assertEquals("Python", student.getTechnicalSkills().get(1));
        assertEquals("SQL", student.getTechnicalSkills().get(2));
        assertEquals("Machine Learning", student.getTechnicalSkills().get(3));
    }

    @Test
    @DisplayName("Should verify equals and hashCode contract based on studentId")
    public void testEqualsAndHashCode() {
        Student s1 = new Student(101, "Alice", "alice@kiet.edu", "CSE", 90.0, 90.0, List.of("Java"));
        Student s2 = new Student(101, "Alice Copy", "other@kiet.edu", "IT", 70.0, 70.0, List.of("C++"));
        Student s3 = new Student(102, "Bob", "bob@kiet.edu", "CSE", 85.0, 85.0, List.of("Java"));

        // Reflexive
        assertEquals(s1, s1);

        // Symmetric and based on studentId
        assertEquals(s1, s2);
        assertEquals(s2, s1);
        assertEquals(s1.hashCode(), s2.hashCode());

        // Different IDs
        assertNotEquals(s1, s3);
        assertNotEquals(s1.hashCode(), s3.hashCode());
    }

    @Test
    @DisplayName("Should handle null student IDs safely in equals and hashCode without throwing NPE")
    public void testEqualsAndHashCodeWithNulls() {
        Student nullId1 = new Student();
        Student nullId2 = new Student();
        Student nonNullId = new Student(101, "Alice", "alice@kiet.edu", "CSE", 90.0, 90.0, List.of("Java"));

        // Comparing two instances with null ID
        assertEquals(nullId1, nullId2);
        assertEquals(nullId1.hashCode(), nullId2.hashCode());
        assertEquals(0, nullId1.hashCode());

        // Comparing null ID with non-null ID
        assertNotEquals(nullId1, nonNullId);
        assertNotEquals(nonNullId, nullId1);

        // Comparing with null object
        assertFalse(nullId1.equals(null));
        assertFalse(nonNullId.equals(null));
    }

    @Test
    @DisplayName("Should format toString() containing all core fields")
    public void testToString() {
        Student student = new Student(101, "Hamdan", "hamdan@kiet.edu", "CSE", 95.0, 90.0, List.of("Java", "SQL"));
        String str = student.toString();

        assertTrue(str.contains("studentId=101"));
        assertTrue(str.contains("studentName='Hamdan'"));
        assertTrue(str.contains("email='hamdan@kiet.edu'"));
        assertTrue(str.contains("courseName='CSE'"));
        assertTrue(str.contains("programmingScore=95.0"));
        assertTrue(str.contains("mathematicsScore=90.0"));
        assertTrue(str.contains("technicalSkills=[Java, SQL]"));
    }

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    public void testValidationSuccess() {
        Student student = new Student(
                101,
                "Hamdan Khan",
                "hamdan@kiet.edu",
                "Computer Science",
                95.0,
                90.0,
                List.of("Java", "Spring")
        );

        Set<ConstraintViolation<Student>> violations = validator.validate(student);
        assertTrue(violations.isEmpty(), "Valid student should have no constraint violations");
    }

    @Test
    @DisplayName("Should fail validation on invalid fields (blank name, invalid email, out-of-range scores)")
    public void testValidationFailures() {
        Student invalidStudent = new Student(
                null,                   // studentId null
                "",                     // blank name
                "invalid-email-format", // invalid email
                "",                     // blank course
                150.0,                  // score > 100
                -10.0,                  // score < 0
                List.of()               // empty skills
        );

        Set<ConstraintViolation<Student>> violations = validator.validate(invalidStudent);
        assertFalse(violations.isEmpty(), "Invalid student must trigger constraint violations");
        assertTrue(violations.size() >= 6, "Expected at least 6 violations for all invalid attributes");
    }

    @Test
    @DisplayName("Should accept valid boundary scores of exactly 0.0 and 100.0")
    public void testBoundaryValidScores() {
        // Test lower boundary 0.0
        Student lowerBoundaryStudent = new Student(
                103,
                "Boundary Student Zero",
                "zero@kiet.edu",
                "Computer Science",
                0.0,
                0.0,
                List.of("C")
        );
        Set<ConstraintViolation<Student>> zeroViolations = validator.validate(lowerBoundaryStudent);
        assertTrue(zeroViolations.isEmpty(), "Scores of exactly 0.0 should be valid");

        // Test upper boundary 100.0
        Student upperBoundaryStudent = new Student(
                104,
                "Boundary Student Hundred",
                "hundred@kiet.edu",
                "Computer Science",
                100.0,
                100.0,
                List.of("Java")
        );
        Set<ConstraintViolation<Student>> hundredViolations = validator.validate(upperBoundaryStudent);
        assertTrue(hundredViolations.isEmpty(), "Scores of exactly 100.0 should be valid");
    }

    @Test
    @DisplayName("Should reject invalid score -0.1 via @DecimalMin")
    public void testInvalidScoreBelowZero() {
        Student student = new Student(
                105,
                "Negative Score Student",
                "negative@kiet.edu",
                "Computer Science",
                -0.1,
                -0.1,
                List.of("Python")
        );

        Set<ConstraintViolation<Student>> violations = validator.validate(student);
        assertEquals(2, violations.size(), "Both programmingScore and mathematicsScore of -0.1 should trigger violations");

        for (ConstraintViolation<Student> violation : violations) {
            String property = violation.getPropertyPath().toString();
            assertTrue(property.equals("programmingScore") || property.equals("mathematicsScore"),
                    "Violation should be on score fields");
            assertTrue(violation.getMessage().contains("must be at least 0.0"));
        }
    }

    @Test
    @DisplayName("Should reject invalid score 100.1 via @DecimalMax")
    public void testInvalidScoreAboveHundred() {
        Student student = new Student(
                106,
                "Excess Score Student",
                "excess@kiet.edu",
                "Computer Science",
                100.1,
                100.1,
                List.of("Python")
        );

        Set<ConstraintViolation<Student>> violations = validator.validate(student);
        assertEquals(2, violations.size(), "Both programmingScore and mathematicsScore of 100.1 should trigger violations");

        for (ConstraintViolation<Student> violation : violations) {
            String property = violation.getPropertyPath().toString();
            assertTrue(property.equals("programmingScore") || property.equals("mathematicsScore"),
                    "Violation should be on score fields");
            assertTrue(violation.getMessage().contains("cannot exceed 100.0"));
        }
    }
}
