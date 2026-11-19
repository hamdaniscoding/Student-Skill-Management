package com.kiet.studentmanagement.exception;

/**
 * Exception thrown when student business validation rules fail
 * (e.g., scores out of bounds, missing required fields, malformed email).
 */
public class InvalidStudentDataException extends RuntimeException {

    public InvalidStudentDataException(String message) {
        super(message);
    }
}
