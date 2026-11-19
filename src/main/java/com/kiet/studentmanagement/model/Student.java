package com.kiet.studentmanagement.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model and JPA Entity representing a Student in the Student Profile and Skill Development Management System.
 *
 * Implements:
 * 1. Complete Encapsulation (private fields, public getters/setters).
 * 2. Default and Parameterized Constructors.
 * 3. Overridden Object methods (toString, equals, hashCode) handling null IDs safely.
 * 4. Java Collection List&lt;String&gt; for technical skills.
 * 5. Jakarta Validation constraints for examination business rules.
 * 6. JPA annotations for seamless MySQL database persistence in Q2 and Q3.
 */
@Entity
@Table(name = "students")
public class Student {

    /**
     * Unique identification number for the student.
     * Serves as the primary key. Not auto-generated to allow manual entry and duplicate checking.
     */
    @Id
    @NotNull(message = "Student ID is required")
    @Column(name = "student_id", nullable = false, unique = true)
    private Integer studentId;

    /**
     * Full name of the student.
     */
    @NotBlank(message = "Student name cannot be blank")
    @Column(name = "student_name", nullable = false)
    private String studentName;

    /**
     * Official contact email address.
     */
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Academic course or program name (e.g., "Computer Science and Engineering").
     */
    @NotBlank(message = "Course name cannot be blank")
    @Column(name = "course_name", nullable = false)
    private String courseName;

    /**
     * Score obtained in programming assessments (0.0 to 100.0).
     */
    @NotNull(message = "Programming score is required")
    @DecimalMin(value = "0.0", message = "Programming score must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Programming score cannot exceed 100.0")
    @Column(name = "programming_score", nullable = false)
    private Double programmingScore;

    /**
     * Score obtained in mathematics assessments (0.0 to 100.0).
     */
    @NotNull(message = "Mathematics score is required")
    @DecimalMin(value = "0.0", message = "Mathematics score must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Mathematics score cannot exceed 100.0")
    @Column(name = "mathematics_score", nullable = false)
    private Double mathematicsScore;

    /**
     * List of technical skill interests (e.g., ["Java", "Python", "SQL", "Machine Learning"]).
     * Mapped via @ElementCollection into a dedicated table 'student_technical_skills' linked to 'student_id'.
     */
    @NotEmpty(message = "Technical skills list cannot be empty")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "student_technical_skills",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @Column(name = "skill_name", nullable = false)
    private List<String> technicalSkills = new ArrayList<>();

    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    /**
     * Default no-argument constructor.
     * Required by JPA entity specification and JSON serialization libraries.
     */
    public Student() {
        this.technicalSkills = new ArrayList<>();
    }

    /**
     * Parameterized constructor accepting all core student attributes.
     *
     * @param studentId        Unique identification number
     * @param studentName      Full name of the student
     * @param email            Contact email address
     * @param courseName       Academic course name
     * @param programmingScore Score secured in programming (0-100)
     * @param mathematicsScore Score secured in mathematics (0-100)
     * @param technicalSkills  List of technical skill interests
     */
    public Student(Integer studentId, String studentName, String email, String courseName,
                   Double programmingScore, Double mathematicsScore, List<String> technicalSkills) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = (technicalSkills != null) ? new ArrayList<>(technicalSkills) : new ArrayList<>();
    }

    // ==========================================
    // GETTERS AND SETTERS (ENCAPSULATION)
    // ==========================================

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Double getProgrammingScore() {
        return programmingScore;
    }

    public void setProgrammingScore(Double programmingScore) {
        this.programmingScore = programmingScore;
    }

    public Double getMathematicsScore() {
        return mathematicsScore;
    }

    public void setMathematicsScore(Double mathematicsScore) {
        this.mathematicsScore = mathematicsScore;
    }

    public List<String> getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = (technicalSkills != null) ? technicalSkills : new ArrayList<>();
    }

    /**
     * Convenience helper method to add an individual technical skill to the list.
     *
     * @param skill The technical skill to add
     */
    public void addTechnicalSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            if (this.technicalSkills == null) {
                this.technicalSkills = new ArrayList<>();
            }
            this.technicalSkills.add(skill.trim());
        }
    }

    // ==========================================
    // OVERRIDDEN OBJECT METHODS
    // ==========================================

    /**
     * Returns a structured string representation of the Student entity.
     * Useful for debugging, logging, and console display.
     */
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", email='" + email + '\'' +
                ", courseName='" + courseName + '\'' +
                ", programmingScore=" + programmingScore +
                ", mathematicsScore=" + mathematicsScore +
                ", technicalSkills=" + technicalSkills +
                '}';
    }

    /**
     * Compares two Student objects for equality based on their studentId.
     * Handles null student IDs safely to avoid NullPointerException.
     *
     * @param o Object to compare with
     * @return true if both objects are the same reference, or have equal student IDs
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    /**
     * Generates a hash code consistent with the equals method using studentId.
     * Safely returns 0 if studentId is null, preventing NullPointerException.
     *
     * @return integer hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(studentId);
    }
}
