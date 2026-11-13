package com.example.usermanagement.user;

import java.util.List;

/** A stable paging contract independent of Spring Data's internal Page serialization. */
public record UserPageResponse(List<UserResponse> content, int page, int size, long totalElements, int totalPages) {
    public UserPageResponse {
        content = List.copyOf(content);
    }
}
