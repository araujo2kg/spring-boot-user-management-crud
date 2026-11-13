package com.example.usermanagement.user;

public class InvalidPaginationException extends RuntimeException {

    public InvalidPaginationException() {
        super("Page must be non-negative, size 1-100, and offset at most 2147483647.");
    }

}
