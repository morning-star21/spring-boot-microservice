package com.rahul.user.service;

import com.rahul.common.exception.ResourceNotFoundException;
import com.rahul.user.dto.UserDto;
import com.rahul.user.model.User;
import com.rahul.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private User testUser;
    private User testUser2;

    @BeforeEach
    public void setup() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("1234567890")
                .address("123 Test St")
                .build();
        
        testUser2 = User.builder()
                .id(2L)
                .username("testuser2")
                .email("test2@example.com")
                .firstName("Test2")
                .lastName("User2")
                .phoneNumber("0987654321")
                .address("456 Test Ave")
                .build();
    }

    @Test
    public void testGetAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, testUser2));
        
        // Act
        List<UserDto> result = userService.getAllUsers();
        
        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals(2L, result.get(1).getId());
        assertEquals("testuser2", result.get(1).getUsername());
        
        verify(userRepository).findAll();
    }

    @Test
    public void testGetUserById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        UserDto result = userService.getUserById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        
        verify(userRepository).findById(1L);
    }

    @Test
    public void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
        
        verify(userRepository).findById(999L);
    }

    @Test
    public void testGetUserByUsername() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // Act
        UserDto result = userService.getUserByUsername("testuser");
        
        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    public void testGetUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByUsername("nonexistent");
        });
        
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    public void testUpdateUser() {
        // Arrange
        UserDto updateDto = UserDto.builder()
                .username("testuser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .phoneNumber("5555555555")
                .address("Updated Address")
                .build();
        
        User updatedUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .phoneNumber("5555555555")
                .address("Updated Address")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        
        // Act
        UserDto result = userService.updateUser(1L, updateDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("Updated", result.getFirstName());
        assertEquals("Updated Address", result.getAddress());
        
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testUpdateUser_EmailAlreadyExists() {
        // Arrange
        UserDto updateDto = UserDto.builder()
                .username("testuser")
                .email("existing@example.com")
                .firstName("Updated")
                .lastName("User")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        
        // Act
        UserDto result = userService.updateUser(1L, updateDto);
        
        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail()); // Email should not be updated
        assertEquals("Updated", result.getFirstName()); // Other fields should be updated
        
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        userService.deleteUser(1L);
        
        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    public void testDeleteUser_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
        
        verify(userRepository).findById(999L);
        verify(userRepository, never()).delete(any());
    }
}
