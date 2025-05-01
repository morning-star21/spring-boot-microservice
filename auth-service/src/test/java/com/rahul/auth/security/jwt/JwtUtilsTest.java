package com.rahul.auth.security.jwt;

import com.rahul.auth.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    private UserDetailsImpl userDetails;
    private Authentication authentication;

    @BeforeEach
    public void setup() {
        // Set a fixed JWT secret for testing
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKeyWithAtLeast32CharactersForHS256Algorithm");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60000); // 1 minute
        
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));
        
        userDetails = new UserDetailsImpl(
                1L,
                "testuser",
                "test@example.com",
                "password",
                authorities);
        
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    @Test
    public void testGenerateJwtToken() {
        // Act
        String token = jwtUtils.generateJwtToken(authentication);
        
        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testGenerateTokenFromUsername() {
        // Act
        String token = jwtUtils.generateTokenFromUsername("testuser");
        
        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testGetUserNameFromJwtToken() {
        // Arrange
        String token = jwtUtils.generateJwtToken(authentication);
        
        // Act
        String username = jwtUtils.getUserNameFromJwtToken(token);
        
        // Assert
        assertEquals("testuser", username);
    }

    @Test
    public void testValidateJwtToken_ValidToken() {
        // Arrange
        String token = jwtUtils.generateJwtToken(authentication);
        
        // Act
        boolean isValid = jwtUtils.validateJwtToken(token);
        
        // Assert
        assertTrue(isValid);
    }

    @Test
    public void testValidateJwtToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.string";
        
        // Act
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);
        
        // Assert
        assertFalse(isValid);
    }

    @Test
    public void testValidateJwtToken_ExpiredToken() throws Exception {
        // Arrange
        // Set a very short expiration time
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 1); // 1 millisecond
        String token = jwtUtils.generateJwtToken(authentication);
        
        // Wait for token to expire
        Thread.sleep(10);
        
        // Act
        boolean isValid = jwtUtils.validateJwtToken(token);
        
        // Assert
        assertFalse(isValid);
    }
}
