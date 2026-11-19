package com.kiet.studentmanagement.service;

import com.kiet.studentmanagement.dto.StudentAssessmentResponse;
import com.kiet.studentmanagement.exception.DuplicateStudentIdException;
import com.kiet.studentmanagement.exception.InvalidStudentDataException;
import com.kiet.studentmanagement.exception.StudentNotFoundException;
import com.kiet.studentmanagement.model.Student;
import com.kiet.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Service class encapsulating business logic for the Student Profile and Skill Development Management System.
 *
 * Implements:
 * 1. Average score calculation.
 * 2. Performance categorization.
 * 3. Stronger subject identification.
 * 4. Multi-criteria searching (ID, email, course).
 * 5. Merit list ranking using Java Comparators.
 * 6. Skill group identification and exact skill set matching using Java Collections.
 * 7. Programming improvement targeting (score < 50 and math >= 75).
 * 8. Duplicate ID prevention and comprehensive score/data validation.
 */
@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // =========================================================================
    // REQUIREMENT 1: Calculate Each Student's Average Score
    // =========================================================================

    /**
     * Calculates the average score between programming and mathematics.
     * Formula: (programmingScore + mathematicsScore) / 2.0
     *
     * @param student The student entity
     * @return Calculated average score as Double
     * @throws InvalidStudentDataException if student or required scores are null
     */
    public Double calculateAverage(Student student) {
        if (student == null) {
            throw new InvalidStudentDataException("Student entity cannot be null.");
        }
        if (student.getProgrammingScore() == null || student.getMathematicsScore() == null) {
            throw new InvalidStudentDataException("Programming and mathematics scores must both be present to calculate average.");
        }
        return (student.getProgrammingScore() + student.getMathematicsScore()) / 2.0;
    }

    // =========================================================================
    // REQUIREMENT 2: Assign Performance Category
    // =========================================================================

    /**
     * Categorizes a student based on calculated average score:
     * - 85.0 or above: "Excellent"
     * - 70.0 to below 85.0: "Good"
     * - 50.0 to below 70.0: "Satisfactory"
     * - Below 50.0: "Needs Improvement"
     *
     * @param student The student entity
     * @return Performance category string
     */
    public String getPerformanceCategory(Student student) {
        Double average = calculateAverage(student);

        if (average >= 85.0) {
            return "Excellent";
        } else if (average >= 70.0) {
            return "Good";
        } else if (average >= 50.0) {
            return "Satisfactory";
        } else {
            return "Needs Improvement";
        }
    }

    // =========================================================================
    // REQUIREMENT 3: Identify Stronger Subject
    // =========================================================================

    /**
     * Compares programming and mathematics scores:
     * - Returns "Programming" if programmingScore > mathematicsScore
     * - Returns "Mathematics" if mathematicsScore > programmingScore
     * - Returns "Tie" if both scores are equal
     *
     * @param student The student entity
     * @return Stronger subject comparison result
     */
    public String getStrongerSubject(Student student) {
        if (student == null) {
            throw new InvalidStudentDataException("Student entity cannot be null.");
        }
        if (student.getProgrammingScore() == null || student.getMathematicsScore() == null) {
            throw new InvalidStudentDataException("Both programming and mathematics scores are required.");
        }

        int comparison = Double.compare(student.getProgrammingScore(), student.getMathematicsScore());
        if (comparison > 0) {
            return "Programming";
        } else if (comparison < 0) {
            return "Mathematics";
        } else {
            return "Tie";
        }
    }

    // =========================================================================
    // REQUIREMENT 4: Search Students by ID, Email, or Course
    // =========================================================================

    /**
     * Searches for a student by primary key ID.
     *
     * @param studentId Unique student ID
     * @return Found Student entity
     * @throws StudentNotFoundException if no student matches the ID
     */
    public Student getStudentById(Integer studentId) {
        if (studentId == null) {
            throw new InvalidStudentDataException("Student ID cannot be null.");
        }
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));
    }

    /**
     * Searches for students by email address (case-insensitive).
     *
     * @param email Email address to search
     * @return List of matching students
     */
    public List<Student> searchByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidStudentDataException("Search email cannot be blank.");
        }
        return studentRepository.findByEmailIgnoreCase(email.trim());
    }

    /**
     * Searches for students enrolled in a specific course (case-insensitive).
     *
     * @param courseName Course name to search
     * @return List of matching students
     */
    public List<Student> searchByCourse(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new InvalidStudentDataException("Search course name cannot be blank.");
        }
        return studentRepository.findByCourseNameIgnoreCase(courseName.trim());
    }

    /**
     * Retrieves all persisted student records.
     *
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // =========================================================================
    // REQUIREMENT 5: Generate Merit List
    // =========================================================================

    /**
     * Generates a merit list of students sorted in descending order of average score.
     * Ties in average score are broken consistently using student ID in ascending order.
     *
     * @return List of students ordered by merit
     */
    public List<Student> getMeritList() {
        List<Student> students = studentRepository.findAll();

        // Comparator: Sort by calculated average descending; tie-break by studentId ascending
        Comparator<Student> meritComparator = (s1, s2) -> {
            Double avg1 = calculateAverage(s1);
            Double avg2 = calculateAverage(s2);
            int scoreCompare = Double.compare(avg2, avg1); // descending
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return Objects.compare(s1.getStudentId(), s2.getStudentId(), Comparator.nullsLast(Integer::compareTo));
        };

        List<Student> sortedList = new ArrayList<>(students);
        sortedList.sort(meritComparator);
        return sortedList;
    }

    // =========================================================================
    // REQUIREMENT 6: Identify Students Who Have Common / Matching Technical Skills
    // =========================================================================

    /**
     * Groups students sharing one or more technical skills.
     * Normalizes skills (trims whitespace, converts to lowercase) and deduplicates entries using Set<String>.
     * Only returns skill groups containing at least two distinct students.
     *
     * @return Map where key is normalized skill and value is list of students possessing that skill
     */
    public Map<String, List<Student>> getStudentsGroupedByCommonSkills() {
        List<Student> allStudents = studentRepository.findAll();
        Map<String, List<Student>> skillMap = new LinkedHashMap<>();

        for (Student student : allStudents) {
            if (student.getTechnicalSkills() != null) {
                // Use Set<String> to normalize and deduplicate within a single student record
                Set<String> normalizedSkills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                for (String skill : student.getTechnicalSkills()) {
                    if (skill != null && !skill.trim().isEmpty()) {
                        normalizedSkills.add(skill.trim());
                    }
                }

                for (String skill : normalizedSkills) {
                    String canonicalKey = skill.toLowerCase();
                    skillMap.computeIfAbsent(canonicalKey, k -> new ArrayList<>()).add(student);
                }
            }
        }

        // Retain only skills shared by at least 2 distinct students
        Map<String, List<Student>> commonSkillsResult = new LinkedHashMap<>();
        for (Map.Entry<String, List<Student>> entry : skillMap.entrySet()) {
            if (entry.getValue().size() >= 2) {
                commonSkillsResult.put(entry.getKey(), entry.getValue());
            }
        }

        return commonSkillsResult;
    }

    /**
     * Identifies students who share an EXACT matching set of technical skills.
     * Clearly distinguishes exact set matches from students who merely share one or more skills.
     *
     * @return Map where key is the Set of normalized skills and value is the list of students with that exact set
     */
    public Map<Set<String>, List<Student>> getStudentsWithExactMatchingSkills() {
        List<Student> allStudents = studentRepository.findAll();
        Map<Set<String>, List<Student>> exactMatchMap = new LinkedHashMap<>();

        for (Student student : allStudents) {
            if (student.getTechnicalSkills() != null) {
                Set<String> normalizedSet = new TreeSet<>();
                for (String skill : student.getTechnicalSkills()) {
                    if (skill != null && !skill.trim().isEmpty()) {
                        normalizedSet.add(skill.trim().toLowerCase());
                    }
                }
                if (!normalizedSet.isEmpty()) {
                    exactMatchMap.computeIfAbsent(normalizedSet, k -> new ArrayList<>()).add(student);
                }
            }
        }

        // Retain only exact skill sets shared by at least 2 students
        Map<Set<String>, List<Student>> result = new LinkedHashMap<>();
        for (Map.Entry<Set<String>, List<Student>> entry : exactMatchMap.entrySet()) {
            if (entry.getValue().size() >= 2) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    // =========================================================================
    // REQUIREMENT 7: Programming Improvement Students
    // =========================================================================

    /**
     * Filters students who need programming improvement.
     * Condition: programmingScore < 50.0 AND mathematicsScore >= 75.0.
     *
     * @return List of students requiring programming improvement
     */
    public List<Student> getStudentsNeedingProgrammingImprovement() {
        List<Student> allStudents = studentRepository.findAll();
        List<Student> improvementList = new ArrayList<>();

        for (Student student : allStudents) {
            if (student.getProgrammingScore() != null && student.getMathematicsScore() != null) {
                if (student.getProgrammingScore() < 50.0 && student.getMathematicsScore() >= 75.0) {
                    improvementList.add(student);
                }
            }
        }

        return improvementList;
    }

    // =========================================================================
    // REQUIREMENT 8: Prevent Duplicate IDs and Validate Scores
    // =========================================================================

    /**
     * Validates student data, enforces duplicate ID prevention, and persists the student in MySQL.
     *
     * @param student The student entity to save
     * @return Persisted Student entity
     * @throws DuplicateStudentIdException if student ID already exists
     * @throws InvalidStudentDataException if scores or fields violate validation rules
     */
    @Transactional
    public Student saveStudent(Student student) {
        if (student == null) {
            throw new InvalidStudentDataException("Student entity cannot be null.");
        }

        // Validate Student ID
        if (student.getStudentId() == null) {
            throw new InvalidStudentDataException("Student ID cannot be null.");
        }
        if (studentRepository.existsById(student.getStudentId())) {
            throw new DuplicateStudentIdException("Student with ID " + student.getStudentId() + " already exists. Duplicate ID rejected.");
        }

        // Validate Student Name
        if (student.getStudentName() == null || student.getStudentName().trim().isEmpty()) {
            throw new InvalidStudentDataException("Student name cannot be blank.");
        }

        // Validate Email
        if (student.getEmail() == null || student.getEmail().trim().isEmpty() || !student.getEmail().contains("@")) {
            throw new InvalidStudentDataException("Valid email address is required.");
        }

        // Validate Course Name
        if (student.getCourseName() == null || student.getCourseName().trim().isEmpty()) {
            throw new InvalidStudentDataException("Course name cannot be blank.");
        }

        // Validate Scores: 0.0 <= score <= 100.0
        if (student.getProgrammingScore() == null) {
            throw new InvalidStudentDataException("Programming score is required.");
        }
        if (student.getProgrammingScore() < 0.0 || student.getProgrammingScore() > 100.0) {
            throw new InvalidStudentDataException("Programming score must be between 0.0 and 100.0. Provided: " + student.getProgrammingScore());
        }

        if (student.getMathematicsScore() == null) {
            throw new InvalidStudentDataException("Mathematics score is required.");
        }
        if (student.getMathematicsScore() < 0.0 || student.getMathematicsScore() > 100.0) {
            throw new InvalidStudentDataException("Mathematics score must be between 0.0 and 100.0. Provided: " + student.getMathematicsScore());
        }

        // Validate Technical Skills
        if (student.getTechnicalSkills() == null || student.getTechnicalSkills().isEmpty()) {
            throw new InvalidStudentDataException("Technical skills list must contain at least one skill.");
        }

        return studentRepository.save(student);
    }

    /**
     * Updates only the scores and technical skills of an existing student.
     * Preserves student ID, student name, email, and course name without alteration.
     *
     * @param studentId        ID of the student to update
     * @param programmingScore New programming score (0.0 to 100.0)
     * @param mathematicsScore New mathematics score (0.0 to 100.0)
     * @param technicalSkills  New list of technical skills (non-empty)
     * @return Updated and persisted Student entity
     * @throws StudentNotFoundException    if the student ID does not exist
     * @throws InvalidStudentDataException if scores or skills are invalid
     */
    @Transactional
    public Student updateStudentScoresAndSkills(Integer studentId, Double programmingScore,
                                               Double mathematicsScore, List<String> technicalSkills) {
        Student existingStudent = getStudentById(studentId);

        // Validate scores
        if (programmingScore == null || programmingScore < 0.0 || programmingScore > 100.0) {
            throw new InvalidStudentDataException("Programming score must be between 0.0 and 100.0. Provided: " + programmingScore);
        }
        if (mathematicsScore == null || mathematicsScore < 0.0 || mathematicsScore > 100.0) {
            throw new InvalidStudentDataException("Mathematics score must be between 0.0 and 100.0. Provided: " + mathematicsScore);
        }

        // Validate skills
        if (technicalSkills == null || technicalSkills.isEmpty()) {
            throw new InvalidStudentDataException("Technical skills list must contain at least one skill.");
        }

        // Update ONLY scores and technical skills; ID, Name, Email, and Course remain unchanged
        existingStudent.setProgrammingScore(programmingScore);
        existingStudent.setMathematicsScore(mathematicsScore);
        existingStudent.setTechnicalSkills(technicalSkills);

        return studentRepository.save(existingStudent);
    }

    /**
     * Generates a comprehensive performance assessment response for a student.
     *
     * @param studentId ID of the student
     * @return StudentAssessmentResponse containing calculated averages, category, and stronger subject
     */
    public StudentAssessmentResponse getStudentAssessment(Integer studentId) {
        Student student = getStudentById(studentId);
        Double average = calculateAverage(student);
        String category = getPerformanceCategory(student);
        String strongerSubject = getStrongerSubject(student);

        return new StudentAssessmentResponse(
                student.getStudentId(),
                student.getStudentName(),
                student.getCourseName(),
                student.getProgrammingScore(),
                student.getMathematicsScore(),
                average,
                category,
                strongerSubject
        );
    }

    /**
     * Deletes a student by ID.
     *
     * @param studentId The ID of the student to delete
     * @throws StudentNotFoundException if the student does not exist
     */
    @Transactional
    public void deleteStudent(Integer studentId) {
        if (studentId == null || !studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException("Cannot delete: Student not found with ID: " + studentId);
        }
        studentRepository.deleteById(studentId);
    }
}
