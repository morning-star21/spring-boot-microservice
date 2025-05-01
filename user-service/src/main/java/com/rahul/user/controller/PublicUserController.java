package com.rahul.user.controller;

import com.rahul.common.dto.ApiResponse;
import com.rahul.user.dto.UserDto;
import com.rahul.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/public")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public User Information", description = "Public API for accessing user information without authentication")
public class PublicUserController {
    private final UserService userService;

    @GetMapping("/username/{username}")
    @Operation(summary = "Get public user info by username", description = "Retrieves public user information by username without requiring authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<com.rahul.common.dto.ApiResponse<UserDto>> getPublicUserByUsername(
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        log.info("Fetching public user info with username: {}", username);
        UserDto user = userService.getUserByUsername(username);
        return ResponseEntity.ok(com.rahul.common.dto.ApiResponse.success(user, "User retrieved successfully"));
    }
}
