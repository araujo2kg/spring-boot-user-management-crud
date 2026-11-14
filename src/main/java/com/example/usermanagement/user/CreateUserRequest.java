package com.example.usermanagement.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record CreateUserRequest(@NotBlank @Size(max = 100) @Schema(example = "Ana Silva") String name,
        @NotBlank @Email @Size(max = 254) @Schema(example = "ana@example.com") String email) {
    public CreateUserRequest {
        name = name == null ? null : name.strip();
        email = email == null ? null : email.strip().toLowerCase(Locale.ROOT);
    }
}
