package com.rahul.user.security.jwt;

import com.rahul.common.dto.ApiResponse;
import com.rahul.user.client.AuthServiceClient;
import com.rahul.user.dto.TokenValidationRequest;
import com.rahul.user.dto.TokenValidationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private AuthServiceClient authServiceClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                // Validate token by calling auth service
                TokenValidationRequest validationRequest = new TokenValidationRequest(jwt);
                ApiResponse<TokenValidationResponse> validationResponse = authServiceClient.validateToken(validationRequest);
                
                if (validationResponse.getData() != null && validationResponse.getData().isValid()) {
                    String username = validationResponse.getData().getUsername();
                    TokenValidationResponse tokenData = validationResponse.getData();
                    
                    // Get roles safely
                    List<String> roles = new ArrayList<>();
                    if (tokenData.getClaims() != null && tokenData.getClaims().get("roles") instanceof List) {
                        roles = (List<String>) tokenData.getClaims().get("roles");
                    }

                    // Convert to authorities
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.toUpperCase()))
                    .collect(Collectors.toList());
                    
                            
                    // Create authentication object
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    validationResponse.getData().getUsername(),
                                    null,
                                    authorities
                            );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Cannot set user authentication: {}", e.getMessage());
            
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Unauthorized")
                    .error(e.getMessage())
                    .build();
                    
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // For LocalDateTime serialization
            mapper.writeValue(response.getOutputStream(), apiResponse);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
