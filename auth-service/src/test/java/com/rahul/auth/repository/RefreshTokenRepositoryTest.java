package com.rahul.auth.repository;

import com.rahul.auth.model.RefreshToken;
import com.rahul.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByToken() {
        // Arrange
        User user = User.builder()
                .username("tokenuser")
                .email("token@example.com")
                .password("password")
                .build();
        
        userRepository.save(user);
        
        RefreshToken refreshToken = RefreshToken.builder()
                .token("test_token")
                .user(user)
                .expiryDate(Instant.now().plusMillis(60000))
                .build();
        
        refreshTokenRepository.save(refreshToken);
        
        // Act
        Optional<RefreshToken> result = refreshTokenRepository.findByToken("test_token");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("test_token", result.get().getToken());
        assertEquals(user.getId(), result.get().getUser().getId());
    }

    @Test
    public void testFindByToken_NotFound() {
        // Act
        Optional<RefreshToken> result = refreshTokenRepository.findByToken("nonexistent_token");
        
        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteByUser() {
        // Arrange
        User user = User.builder()
                .username("deleteuser")
                .email("delete@example.com")
                .password("password")
                .build();
        
        userRepository.save(user);
        
        RefreshToken refreshToken = RefreshToken.builder()
                .token("delete_token")
                .user(user)
                .expiryDate(Instant.now().plusMillis(60000))
                .build();
        
        refreshTokenRepository.save(refreshToken);
        
        // Act
        int deletedCount = refreshTokenRepository.deleteByUser(user);
        
        // Assert
        assertEquals(1, deletedCount);
        assertFalse(refreshTokenRepository.findByToken("delete_token").isPresent());
    }
}
