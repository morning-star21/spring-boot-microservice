package com.rahul.auth.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request payload")
public class LoginRequest {
    @NotBlank
    @Schema(description = "Username for login", example = "johndoe", required = true)
    private String username;

    @NotBlank
    @Schema(description = "Password for login", example = "password123", required = true)
    private String password;
}
