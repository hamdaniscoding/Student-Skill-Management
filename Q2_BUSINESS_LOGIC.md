# Q2 Business Logic & Java Collections Viva Guide

This document summarizes the technical decisions, algorithms, and Java Collections framework implementations used in `StudentService.java` for the CS336B examination and viva.

---

## 1. Java Collections Framework Choices for Viva

| Collection Type | Exact Usage in Project | Why Chosen (Viva Justification) |
|---|---|---|
| **`List<Student>`** | Return type for `getAllStudents()`, `getMeritList()`, `searchByEmail()`, `searchByCourse()`, `getStudentsNeedingProgrammingImprovement()` | Preserves insertion and sorted ordering. Ideal for ordered sequential processing and database query result sets. |
| **`List<String>`** | `technicalSkills` attribute in `Student` entity | Represents an ordered collection of skills per student. Directly supported by JPA via `@ElementCollection` and mapped to `student_technical_skills`. |
| **`Set<String>`** | Skill normalization inside `getStudentsGroupedByCommonSkills()` and keys in `getStudentsWithExactMatchingSkills()` | Guarantees uniqueness. Automatically eliminates duplicate skills entered for the same student (e.g. `["Java", "java", " Java "]`). Case-insensitive `TreeSet` enables reliable set equality comparisons. |
| **`Map<String, List<Student>>`** | Return type for `getStudentsGroupedByCommonSkills()` | Implements a multi-value index (inverted index). The key represents a single normalized skill, and the value is a list of all students possessing that skill. |
| **`Map<Set<String>, List<Student>>`** | Return type for `getStudentsWithExactMatchingSkills()` | Groups students by their exact composite skill signature. Since `Set` implements order-independent `equals()` and `hashCode()`, students with `["Java", "Python"]` and `["Python", "Java"]` are mapped to the same key. |
| **`Comparator<Student>`** | Used in `getMeritList()` for sorting | Allows custom comparison logic without modifying the `Student` class. Compares students by calculated average score descending, with a deterministic secondary sort by `studentId` ascending. |

---

## 2. Business Logic Implementation Details

### Requirement 1: Average Score
- Formula: `(programmingScore + mathematicsScore) / 2.0`
- Calculated dynamically on demand. Not stored redundantly in the database to avoid data inconsistency.

### Requirement 2: Performance Categorization
Evaluated using strict boundary checks:
- $85.0 \le \text{avg} \le 100.0 \implies$ `"Excellent"`
- $70.0 \le \text{avg} < 85.0 \implies$ `"Good"`
- $50.0 \le \text{avg} < 70.0 \implies$ `"Satisfactory"`
- $\text{avg} < 50.0 \implies$ `"Needs Improvement"`

### Requirement 3: Stronger Subject Identification
Uses `Double.compare(programmingScore, mathematicsScore)`:
- $> 0 \implies$ `"Programming"`
- $< 0 \implies$ `"Mathematics"`
- $== 0 \implies$ `"Tie"`

### Requirement 4: Multi-Criteria Search
- `getStudentById`: Primary key lookup using `findById`. Throws `StudentNotFoundException` (HTTP 404) if absent.
- `searchByEmail`: Derived query `findByEmailIgnoreCase(email)`. Returns `List<Student>`.
- `searchByCourse`: Derived query `findByCourseNameIgnoreCase(courseName)`. Returns `List<Student>`.

### Requirement 5: Merit List Sorting
```java
Comparator<Student> meritComparator = (s1, s2) -> {
    int scoreCompare = Double.compare(calculateAverage(s2), calculateAverage(s1)); // Descending
    if (scoreCompare != 0) {
        return scoreCompare;
    }
    return Objects.compare(s1.getStudentId(), s2.getStudentId(), Comparator.nullsLast(Integer::compareTo));
};
```

### Requirement 6: Technical Skills Comparison
1. **Common Skills (`getStudentsGroupedByCommonSkills`)**:
   - Skills are trimmed and converted to lower case.
   - Deduped per student with `Set<String>`.
   - Aggregated into `Map<String, List<Student>>`.
   - Filtered with `entry.getValue().size() >= 2` to only retain skills shared by at least two distinct students.
2. **Exact Matching Skills (`getStudentsWithExactMatchingSkills`)**:
   - Normalized unique skill set is extracted as `Set<String>`.
   - Grouped into `Map<Set<String>, List<Student>>`.
   - Filtered where group size $\ge 2$.

### Requirement 7: Programming Improvement Filter
- Condition: `programmingScore < 50.0 && mathematicsScore >= 75.0`
- Targets students with strong quantitative aptitude who lag in programming.

### Requirement 8: Duplicate ID Prevention & Data Validation
- Verifies `studentRepository.existsById(student.getStudentId())`. If true, throws `DuplicateStudentIdException` (HTTP 409 Conflict).
- Validates `0.0 <= score <= 100.0` for both scores. If violated or null, throws `InvalidStudentDataException` (HTTP 400 Bad Request).

---

## 3. Exception Handling Architecture

- `StudentNotFoundException` $\to$ HTTP 404 NOT FOUND
- `DuplicateStudentIdException` $\to$ HTTP 409 CONFLICT
- `InvalidStudentDataException` $\to$ HTTP 400 BAD REQUEST
- Centralized via `@RestControllerAdvice` in `GlobalExceptionHandler.java`.
