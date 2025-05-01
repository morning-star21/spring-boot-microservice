package com.rahul.auth.controller;

import com.rahul.auth.dto.TokenValidationRequest;
import com.rahul.auth.dto.TokenValidationResponse;
import com.rahul.auth.security.jwt.JwtUtils;
import com.rahul.common.dto.ApiResponse;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/token")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Token Validation", description = "Internal API for token validation between services")
public class TokenValidationController {

    private final JwtUtils jwtUtils;

    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Internal endpoint for validating JWT tokens between services")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token validation result",
                content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<com.rahul.common.dto.ApiResponse<TokenValidationResponse>> validateToken(
            @Parameter(description = "Token to validate", required = true)
            @RequestBody TokenValidationRequest request) {
        log.info("Validating token for internal service call");
        boolean isValid = jwtUtils.validateJwtToken(request.getToken());
        
        TokenValidationResponse response = new TokenValidationResponse();
        if (isValid) {
            String username = jwtUtils.getUserNameFromJwtToken(request.getToken());
            Claims claims = jwtUtils.getAllClaimsFromToken(request.getToken());
            response.setValid(true);
            response.setUsername(username);
            response.setClaims(claims);
        } else {
            response.setValid(false);
        }
        
        return ResponseEntity.ok(com.rahul.common.dto.ApiResponse.success(response));
    }
}
