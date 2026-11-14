package com.example.usermanagement.error;

import com.example.usermanagement.user.DuplicateEmailException;
import com.example.usermanagement.user.InvalidPaginationException;
import com.example.usermanagement.user.UserNotFoundException;
import java.util.Comparator;
import java.util.List;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    ProblemDetail notFound(UserNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    ProblemDetail duplicateEmail(DuplicateEmailException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(InvalidPaginationException.class)
    ProblemDetail invalidPagination(InvalidPaginationException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * Classifies only the known email constraint, other integrity failures remain server
     * errors.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail integrityFailure(DataIntegrityViolationException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation) {
                String name = violation.getConstraintName();
                if (name != null && (name.equals("uk_users_email") || name.endsWith(".uk_users_email"))) {
                    return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Email is already registered.");
                }
            }
        }

        return serverError(exception);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, "Request validation failed.");
        List<FieldError> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
            .sorted(Comparator.comparing(FieldError::field).thenComparing(FieldError::message))
            .toList();

        problem.setProperty("errors", errors);
        return handleExceptionInternal(exception, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        if (body == null) {
            body = ProblemDetail.forStatusAndDetail(status,
                    status.is4xxClientError() ? "Request could not be processed." : "An unexpected error occurred.");
        }
        if (body instanceof ProblemDetail problem) {
            problem.setDetail(
                    status.is4xxClientError() ? "Request could not be processed." : "An unexpected error occurred.");
        }

        return super.handleExceptionInternal(exception, body, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail unexpected(Exception exception) {
        return serverError(exception);
    }

    private ProblemDetail serverError(Exception exception) {
        // Keep submitted values and SQL out of logs.
        StackTraceElement[] frames = exception.getStackTrace();
        LOG.error("Request failed: {} at {}", exception.getClass().getSimpleName(),
                frames.length == 0 ? "unknown location" : frames[0]);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    public record FieldError(String field, String message) {
    }

}
