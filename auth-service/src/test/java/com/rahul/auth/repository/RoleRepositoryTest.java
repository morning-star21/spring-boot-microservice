package com.rahul.auth.repository;

import com.rahul.auth.model.ERole;
import com.rahul.auth.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testFindByName() {
        // Arrange
        Role role = new Role(null, ERole.ROLE_USER);
        roleRepository.save(role);
        
        // Act
        Optional<Role> result = roleRepository.findByName(ERole.ROLE_USER);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(ERole.ROLE_USER, result.get().getName());
    }

    @Test
    public void testFindByName_NotFound() {
        // Arrange - ensure ROLE_ADMIN doesn't exist
        roleRepository.findByName(ERole.ROLE_ADMIN)
                .ifPresent(role -> roleRepository.delete(role));
        
        // Act
        Optional<Role> result = roleRepository.findByName(ERole.ROLE_ADMIN);
        
        // Assert
        assertFalse(result.isPresent());
    }
}
