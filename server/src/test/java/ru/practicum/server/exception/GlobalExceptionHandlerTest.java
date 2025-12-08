package ru.practicum.server.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFoundReturns404WithMessage() {
        NotFoundException exception = new NotFoundException("Entity not found");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", response.getBody().getError());
    }

    @Test
    void handleBadRequestReturns400WithMessage() {
        BadRequestException exception = new BadRequestException("Invalid request");

        ResponseEntity<ErrorResponse> response = handler.handleBadRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request", response.getBody().getError());
    }

    @Test
    void handleConflictReturns409WithMessage() {
        ConflictException exception = new ConflictException("Conflicting state");

        ResponseEntity<ErrorResponse> response = handler.handleConflict(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflicting state", response.getBody().getError());
    }

    @Test
    void handleResponseStatusExceptionUsesStatusAndReason() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");

        ResponseEntity<ErrorResponse> response = handler.handleResponseStatusException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().getError());
    }

    @Test
    void handleUnexpectedExceptionReturns500WithMessage() {
        Exception exception = new Exception("Unexpected failure");

        ResponseEntity<ErrorResponse> response = handler.handleUnexpectedException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected failure", response.getBody().getError());
    }

    @Test
    void handleUnexpectedExceptionReturnsDefaultMessageWhenNull() {
        Exception exception = new Exception((String) null);

        ResponseEntity<ErrorResponse> response = handler.handleUnexpectedException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody().getError());
    }
}
