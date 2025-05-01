package com.rahul.user.controller;

import com.rahul.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users/status")
public class StatusController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "user-service");
        status.put("status", "UP");
        status.put("version", "1.0");
        
        return ResponseEntity.ok(ApiResponse.success(status, "Service is running"));
    }
}
