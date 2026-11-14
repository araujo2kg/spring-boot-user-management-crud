package com.example.usermanagement.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record UpdateUserRequest(@NotBlank @Size(max = 100) @Schema(example = "Ana Santos") String name,
        @NotBlank @Email @Size(max = 254) @Schema(example = "ana.santos@example.com") String email) {
    public UpdateUserRequest {
        name = name == null ? null : name.strip();
        email = email == null ? null : email.strip().toLowerCase(Locale.ROOT);
    }
}
