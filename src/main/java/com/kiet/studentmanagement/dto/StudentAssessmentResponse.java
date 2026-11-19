package com.kiet.studentmanagement.dto;

/**
 * Data Transfer Object (DTO) for returning a student's comprehensive performance assessment,
 * including calculated average score, performance category, and stronger subject.
 */
public class StudentAssessmentResponse {

    private Integer studentId;
    private String studentName;
    private String courseName;
    private Double programmingScore;
    private Double mathematicsScore;
    private Double averageScore;
    private String performanceCategory;
    private String strongerSubject;

    public StudentAssessmentResponse() {
    }

    public StudentAssessmentResponse(Integer studentId, String studentName, String courseName,
                                     Double programmingScore, Double mathematicsScore,
                                     Double averageScore, String performanceCategory,
                                     String strongerSubject) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.averageScore = averageScore;
        this.performanceCategory = performanceCategory;
        this.strongerSubject = strongerSubject;
    }

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

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public String getPerformanceCategory() {
        return performanceCategory;
    }

    public void setPerformanceCategory(String performanceCategory) {
        this.performanceCategory = performanceCategory;
    }

    public String getStrongerSubject() {
        return strongerSubject;
    }

    public void setStrongerSubject(String strongerSubject) {
        this.strongerSubject = strongerSubject;
    }
}
