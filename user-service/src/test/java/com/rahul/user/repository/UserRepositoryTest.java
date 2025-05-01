package com.rahul.user.repository;

import com.rahul.user.model.User;
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
                .firstName("Test")
                .lastName("User")
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
    public void testFindByEmail() {
        // Arrange
        User user = User.builder()
                .username("emailuser")
                .email("email@example.com")
                .firstName("Email")
                .lastName("User")
                .build();
        
        userRepository.save(user);
        
        // Act
        Optional<User> result = userRepository.findByEmail("email@example.com");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("emailuser", result.get().getUsername());
        assertEquals("email@example.com", result.get().getEmail());
    }

    @Test
    public void testExistsByUsername() {
        // Arrange
        User user = User.builder()
                .username("existinguser")
                .email("existing@example.com")
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
                .username("emailexists")
                .email("exists@example.com")
                .build();
        
        userRepository.save(user);
        
        // Act & Assert
        assertTrue(userRepository.existsByEmail("exists@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
}
