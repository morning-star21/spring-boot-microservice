package com.rahul.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Data Transfer Object")
public class UserDto {
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 20)
    @Schema(description = "Username for login", example = "johndoe", required = true)
    private String username;
    
    @NotBlank
    @Size(max = 50)
    @Email
    @Schema(description = "Email address", example = "john.doe@example.com", required = true)
    private String email;
    
    @Schema(description = "User's first name", example = "John")
    private String firstName;
    
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;
    
    @Schema(description = "User's address", example = "123 Main St, City, Country")
    private String address;
}
