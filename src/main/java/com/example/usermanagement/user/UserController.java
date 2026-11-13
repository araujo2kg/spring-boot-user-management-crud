package com.example.usermanagement.user;

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
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = users.create(request);
        return ResponseEntity.created(URI.create("/users/" + user.id())).body(user);
    }

    @GetMapping
    public UserPageResponse list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return users.list(page, size);
    }

    @GetMapping("/{id}")
    public UserResponse find(@PathVariable @Positive long id) {
        return users.find(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable @Positive long id, @Valid @RequestBody UpdateUserRequest request) {
        return users.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive long id) {
        users.delete(id);
        return ResponseEntity.noContent().build();
    }

}
