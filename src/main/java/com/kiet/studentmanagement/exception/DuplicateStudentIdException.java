package com.kiet.studentmanagement.exception;

/**
 * Exception thrown when attempting to register or save a student with an ID that already exists.
 * Preserves user-supplied ID uniqueness.
 */
public class DuplicateStudentIdException extends RuntimeException {

    public DuplicateStudentIdException(String message) {
        super(message);
    }
}
