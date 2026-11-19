# Student Profile and Skill Development Management System

A production-grade, examination-ready Spring Boot 3 application developed for the **CS336B Object Oriented Programming Using Java** examination.

The system maintains student academic records, evaluates programming and mathematics performance, manages technical skill interests using Java Collections, persists data to MySQL using Spring Data JPA, and implements comprehensive business logic.

---

## 1. Technology Stack

- **Java:** 21 LTS
- **Spring Boot:** 3.3.5
- **Build Tool:** Maven (with `mvnw` wrapper included)
- **Persistence:** Spring Data JPA / Hibernate 6
- **Database:** MySQL 8.x
- **Validation:** Jakarta Validation (`@NotNull`, `@NotBlank`, `@Email`, `@DecimalMin`, `@DecimalMax`, `@NotEmpty`)
- **Testing:** JUnit 5, Spring Boot Test

---

## 2. Database Setup & Configuration

### A. Database Name
The application connects to:
```sql
student_skill_db
```

### B. Environment Variables
The application reads database credentials from environment variables with local defaults:

| Variable | Description | Default Fallback |
|---|---|---|
| `DB_HOST` | MySQL host address | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | Database schema name | `student_skill_db` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | Set via `DB_PASSWORD` environment variable or local config |

> [!WARNING]
> **Security Notice:** The `application.properties` file contains local database configuration. Do not commit sensitive credentials or real production passwords to public version control repositories (e.g. GitHub). For production environments, always inject credentials via secure environment variables.

### C. Database Tables
Hibernate automatically generates and manages two normalized relational tables:

1. **`students`**:
   - `student_id` (INT, Primary Key, user-supplied)
   - `student_name` (VARCHAR, NOT NULL)
   - `email` (VARCHAR, NOT NULL)
   - `course_name` (VARCHAR, NOT NULL)
   - `programming_score` (DOUBLE, NOT NULL)
   - `mathematics_score` (DOUBLE, NOT NULL)

2. **`student_technical_skills`** (ElementCollection child table):
   - `student_id` (INT, Foreign Key referencing `students.student_id`)
   - `skill_name` (VARCHAR, NOT NULL)

---

## 3. Application Startup Instructions

### Prerequisites
- JDK 21 installed and on `PATH`
- MySQL Server 8.x running on port `3306`

### Running the Application
From the project root:

**On Windows (PowerShell / Command Prompt):**
```powershell
.\mvnw.cmd clean spring-boot:run
```

**Running Tests:**
```powershell
.\mvnw.cmd clean test
```

**Packaging the JAR:**
```powershell
.\mvnw.cmd clean package
```

The executable JAR is generated in `target/Student-Skill-Management-1.0.0.jar`.

---

## 4. Q2 Business Logic Requirements

All business rules reside inside `StudentService.java` (`com.kiet.studentmanagement.service`):

1. **Requirement 1 — Average Score Calculation:**
   - Method: `calculateAverage(Student student)`
   - Formula: `(programmingScore + mathematicsScore) / 2.0`
   - Preserves original scores without mutation.

2. **Requirement 2 — Performance Categorization:**
   - Method: `getPerformanceCategory(Student student)`
   - Thresholds:
     - `Average >= 85.0`: **Excellent**
     - `70.0 <= Average < 85.0`: **Good**
     - `50.0 <= Average < 70.0`: **Satisfactory**
     - `Average < 50.0`: **Needs Improvement**

3. **Requirement 3 — Stronger Subject Identification:**
   - Method: `getStrongerSubject(Student student)`
   - Compares programming and math scores: returns `"Programming"`, `"Mathematics"`, or `"Tie"`.

4. **Requirement 4 — Multi-Criteria Student Search:**
   - Search by ID: `getStudentById(Integer studentId)` (throws `StudentNotFoundException` if missing).
   - Search by Email: `searchByEmail(String email)` (case-insensitive derived query).
   - Search by Course: `searchByCourse(String courseName)` (case-insensitive derived query).

5. **Requirement 5 — Merit List Generation:**
   - Method: `getMeritList()`
   - Sorts persisted students by average score descending using `Comparator<Student>`. Ties are resolved consistently using `studentId` in ascending order.

6. **Requirement 6 — Technical Skill Matching:**
   - Common Skills: `getStudentsGroupedByCommonSkills()` returns `Map<String, List<Student>>` containing normalized skills shared by $\ge 2$ students.
   - Exact Skill Sets: `getStudentsWithExactMatchingSkills()` returns `Map<Set<String>, List<Student>>` grouping students possessing the exact identical set of skills ($\ge 2$ students).

7. **Requirement 7 — Programming Improvement Filter:**
   - Method: `getStudentsNeedingProgrammingImprovement()`
   - Filters students matching: `programmingScore < 50.0 AND mathematicsScore >= 75.0`.

8. **Requirement 8 — Duplicate Prevention & Score Validation:**
   - Method: `saveStudent(Student student)`
   - Rejects duplicate primary key IDs via `DuplicateStudentIdException`.
   - Validates scores are between $0.0$ and $100.0$ via `InvalidStudentDataException`.

---

## 5. Project Architecture

```
Student-Skill-Management/
├── src/main/java/com/kiet/studentmanagement/
│   ├── StudentManagementApplication.java  (Spring Boot Main Entry Point)
│   ├── controller/                         (Reserved for Q3 REST Controllers)
│   ├── dto/                                (ErrorResponse payload)
│   ├── exception/                          (Custom exceptions & @RestControllerAdvice)
│   ├── model/                              (Student entity & validation)
│   ├── repository/                         (StudentRepository JpaRepository)
│   └── service/                            (StudentService business logic)
└── src/test/java/com/kiet/studentmanagement/
    ├── model/StudentTest.java             (Q1 Entity & boundary tests)
    └── service/StudentServiceTest.java     (Q2 Business logic & JPA integration tests)
```
