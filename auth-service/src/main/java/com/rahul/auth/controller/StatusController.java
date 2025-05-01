package com.rahul.auth.controller;

import com.rahul.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/status")
public class StatusController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "auth-service");
        status.put("status", "UP");
        status.put("version", "1.0");
        
        return ResponseEntity.ok(ApiResponse.success(status, "Service is running"));
    }
}
