package com.rahul.auth.service;

import com.rahul.auth.exception.TokenRefreshException;
import com.rahul.auth.model.RefreshToken;
import com.rahul.auth.model.User;
import com.rahul.auth.repository.RefreshTokenRepository;
import com.rahul.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private UserRepository userRepository;

    private User testUser;
    private RefreshToken validRefreshToken;
    private RefreshToken expiredRefreshToken;

    @BeforeEach
    public void setup() {
        // Set refresh token duration for testing
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 60000L); // 1 minute
        
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .build();
        
        validRefreshToken = RefreshToken.builder()
                .id(1L)
                .token("valid_refresh_token")
                .user(testUser)
                .expiryDate(Instant.now().plusMillis(60000)) // 1 minute in future
                .build();
        
        expiredRefreshToken = RefreshToken.builder()
                .id(2L)
                .token("expired_refresh_token")
                .user(testUser)
                .expiryDate(Instant.now().minusMillis(60000)) // 1 minute in past
                .build();
    }

    @Test
    public void testFindByToken() {
        // Arrange
        when(refreshTokenRepository.findByToken("valid_refresh_token"))
                .thenReturn(Optional.of(validRefreshToken));
        
        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken("valid_refresh_token");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(validRefreshToken, result.get());
        verify(refreshTokenRepository).findByToken("valid_refresh_token");
    }

    @Test
    public void testCreateRefreshToken() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(1L);
            return token;
        });
        
        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertNotNull(result.getToken());
        assertNotNull(result.getExpiryDate());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));
        
        verify(userRepository).findById(1L);
        verify(refreshTokenRepository).deleteByUser(testUser);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    public void testVerifyExpiration_ValidToken() {
        // Act
        RefreshToken result = refreshTokenService.verifyExpiration(validRefreshToken);
        
        // Assert
        assertEquals(validRefreshToken, result);
    }

    @Test
    public void testVerifyExpiration_ExpiredToken() {
        // Act & Assert
        TokenRefreshException exception = assertThrows(TokenRefreshException.class, () -> {
            refreshTokenService.verifyExpiration(expiredRefreshToken);
        });
        
        assertEquals("Failed for [expired_refresh_token]: Refresh token was expired. Please make a new signin request", 
                exception.getMessage());
        
        verify(refreshTokenRepository).delete(expiredRefreshToken);
    }

    @Test
    public void testDeleteByUserId() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.deleteByUser(testUser)).thenReturn(1);
        
        // Act
        int result = refreshTokenService.deleteByUserId(1L);
        
        // Assert
        assertEquals(1, result);
        verify(userRepository).findById(1L);
        verify(refreshTokenRepository).deleteByUser(testUser);
    }

    @Test
    public void testDeleteByUserId_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.deleteByUserId(999L);
        });
        
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(refreshTokenRepository, never()).deleteByUser(any());
    }
}
