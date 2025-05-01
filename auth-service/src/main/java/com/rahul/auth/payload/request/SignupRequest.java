package com.rahul.auth.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "User registration request payload")
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    @Schema(description = "Username for registration", example = "johndoe", required = true)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    @Schema(description = "Email address", example = "john.doe@example.com", required = true)
    private String email;

    @Schema(description = "User roles", example = "[\"user\", \"admin\"]")
    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    @Schema(description = "Password for registration", example = "password123", required = true)
    private String password;
}
