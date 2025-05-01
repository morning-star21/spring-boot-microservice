package com.rahul.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token validation response")
public class TokenValidationResponse {
    @Schema(description = "Whether the token is valid", example = "true")
    private boolean valid;
    
    @Schema(description = "Username from the token", example = "johndoe")
    private String username;

    @JsonProperty("claims")
    private Map<String, Object> claims;
}
