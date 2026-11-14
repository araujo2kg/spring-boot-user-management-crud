package com.example.usermanagement.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @PostMapping
    @Operation(summary = "Create a user")
    @ApiResponse(responseCode = "201", description = "User created")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = users.create(request);
        return ResponseEntity.created(URI.create("/users/" + user.id())).body(user);
    }

    @GetMapping
    @Operation(summary = "List users", description = "Pages start at 0. Page size must be between 1 and 100.")
    public UserPageResponse list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return users.list(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a user")
    public UserResponse find(@PathVariable @Positive long id) {
        return users.find(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Replace the user's name and email.")
    public UserResponse update(@PathVariable @Positive long id, @Valid @RequestBody UpdateUserRequest request) {
        return users.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @ApiResponse(responseCode = "204", description = "User deleted", content = @Content)
    public ResponseEntity<Void> delete(@PathVariable @Positive long id) {
        users.delete(id);
        return ResponseEntity.noContent().build();
    }

}
