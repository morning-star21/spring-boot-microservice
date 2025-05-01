package com.rahul.user.security.jwt;

import com.rahul.common.dto.ApiResponse;
import com.rahul.user.client.AuthServiceClient;
import com.rahul.user.dto.TokenValidationRequest;
import com.rahul.user.dto.TokenValidationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class AuthTokenFilterTest {

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterInternal_ValidToken() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        
        TokenValidationResponse validationResponse = new TokenValidationResponse(true, "testuser");
        ApiResponse<TokenValidationResponse> apiResponse = ApiResponse.success(validationResponse);
        
        when(authServiceClient.validateToken(any(TokenValidationRequest.class))).thenReturn(apiResponse);
        
        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        
        verify(request).getHeader("Authorization");
        verify(authServiceClient).validateToken(any(TokenValidationRequest.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_InvalidToken() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");
        
        TokenValidationResponse validationResponse = new TokenValidationResponse(false, null);
        ApiResponse<TokenValidationResponse> apiResponse = ApiResponse.success(validationResponse);
        
        when(authServiceClient.validateToken(any(TokenValidationRequest.class))).thenReturn(apiResponse);
        
        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        verify(request).getHeader("Authorization");
        verify(authServiceClient).validateToken(any(TokenValidationRequest.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_NoToken() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);
        
        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        verify(request).getHeader("Authorization");
        verify(authServiceClient, never()).validateToken(any(TokenValidationRequest.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_InvalidTokenFormat() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");
        
        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        verify(request).getHeader("Authorization");
        verify(authServiceClient, never()).validateToken(any(TokenValidationRequest.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_ExceptionDuringValidation() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(authServiceClient.validateToken(any(TokenValidationRequest.class))).thenThrow(new RuntimeException("Service unavailable"));
        
        // Configure response for error handling
        when(response.getOutputStream()).thenReturn(new DummyServletOutputStream());
        
        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        verify(request).getHeader("Authorization");
        verify(authServiceClient).validateToken(any(TokenValidationRequest.class));
        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }
    
    // Helper class for testing
    private static class DummyServletOutputStream extends jakarta.servlet.ServletOutputStream {
        @Override
        public void write(int b) {
            // Do nothing
        }
        
        @Override
        public boolean isReady() {
            return true;
        }
        
        @Override
        public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
            // Do nothing
        }
    }
}
