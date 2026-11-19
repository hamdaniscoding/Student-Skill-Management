package com.kiet.studentmanagement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for updating a student's scores and skills.
 * Restricts updates to only programmingScore, mathematicsScore, and technicalSkills.
 * Student ID, Name, Email, and Course cannot be altered through this request.
 */
public class StudentUpdateRequest {

    @NotNull(message = "Programming score is required")
    @DecimalMin(value = "0.0", message = "Programming score must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Programming score cannot exceed 100.0")
    private Double programmingScore;

    @NotNull(message = "Mathematics score is required")
    @DecimalMin(value = "0.0", message = "Mathematics score must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Mathematics score cannot exceed 100.0")
    private Double mathematicsScore;

    @NotEmpty(message = "Technical skills list cannot be empty")
    private List<String> technicalSkills = new ArrayList<>();

    public StudentUpdateRequest() {
    }

    public StudentUpdateRequest(Double programmingScore, Double mathematicsScore, List<String> technicalSkills) {
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = technicalSkills != null ? new ArrayList<>(technicalSkills) : new ArrayList<>();
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
        this.technicalSkills = technicalSkills != null ? technicalSkills : new ArrayList<>();
    }
}
