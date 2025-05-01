package com.rahul.auth.dto;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token validation response")
public class TokenValidationResponse {
    @Schema(description = "Whether the token is valid", example = "true")
    private boolean valid;
    
    @Schema(description = "Username from the token", example = "johndoe")
    private String username;

    private Claims claims;
}
