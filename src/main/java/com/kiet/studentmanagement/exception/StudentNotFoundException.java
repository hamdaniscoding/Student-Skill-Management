package com.kiet.studentmanagement.exception;

/**
 * Exception thrown when a requested student is not found by ID or other criteria.
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String message) {
        super(message);
    }
}
