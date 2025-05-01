package com.rahul.user.client;

import com.rahul.user.dto.TokenValidationRequest;
import com.rahul.user.dto.TokenValidationResponse;
import com.rahul.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "${auth.service.url:http://localhost:8080}")
public interface AuthServiceClient {
    
    @PostMapping("/api/auth/token/validate")
    ApiResponse<TokenValidationResponse> validateToken(@RequestBody TokenValidationRequest request);
}
