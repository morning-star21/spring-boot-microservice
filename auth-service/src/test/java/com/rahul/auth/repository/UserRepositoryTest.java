package com.rahul.auth.repository;

import com.rahul.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();
        
        userRepository.save(user);
        
        // Act
        Optional<User> result = userRepository.findByUsername("testuser");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    public void testFindByUsername_NotFound() {
        // Act
        Optional<User> result = userRepository.findByUsername("nonexistent");
        
        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testExistsByUsername() {
        // Arrange
        User user = User.builder()
                .username("existinguser")
                .email("existing@example.com")
                .password("password")
                .build();
        
        userRepository.save(user);
        
        // Act & Assert
        assertTrue(userRepository.existsByUsername("existinguser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    public void testExistsByEmail() {
        // Arrange
        User user = User.builder()
                .username("emailuser")
                .email("email@example.com")
                .password("password")
                .build();
        
        userRepository.save(user);
        
        // Act & Assert
        assertTrue(userRepository.existsByEmail("email@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
}
