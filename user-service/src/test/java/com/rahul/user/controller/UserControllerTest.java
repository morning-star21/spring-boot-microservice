package com.rahul.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.user.dto.UserDto;
import com.rahul.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto testUser;
    private UserDto testUser2;

    @BeforeEach
    public void setup() {
        testUser = UserDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("1234567890")
                .address("123 Test St")
                .build();
        
        testUser2 = UserDto.builder()
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
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers() throws Exception {
        // Arrange
        List<UserDto> users = Arrays.asList(testUser, testUser2);
        when(userService.getAllUsers()).thenReturn(users);
        
        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Users retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].username", is("testuser")))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].username", is("testuser2")));
        
        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllUsers_Forbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
        
        verify(userService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetUserById_Self() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUser);
        
        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("User retrieved successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.username", is("testuser")))
                .andExpect(jsonPath("$.data.email", is("test@example.com")));
        
        verify(userService).getUserById(1L);
    }

    @Test
    @WithMockUser(username = "otheruser")
    public void testGetUserById_NotSelf() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
        
        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById_Admin() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUser);
        
        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("User retrieved successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.username", is("testuser")));
        
        verify(userService).getUserById(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetUserByUsername_Self() throws Exception {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        
        // Act & Assert
        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("User retrieved successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.username", is("testuser")));
        
        verify(userService).getUserByUsername("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testUpdateUser_Self() throws Exception {
        // Arrange
        UserDto updateDto = UserDto.builder()
                .username("testuser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .phoneNumber("5555555555")
                .address("Updated Address")
                .build();
        
        UserDto updatedUser = UserDto.builder()
                .id(1L)
                .username("testuser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .phoneNumber("5555555555")
                .address("Updated Address")
                .build();
        
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(updatedUser);
        
        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("User updated successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.email", is("updated@example.com")))
                .andExpect(jsonPath("$.data.firstName", is("Updated")));
        
        verify(userService).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("User deleted successfully")))
                .andExpect(jsonPath("$.data", is("User deleted successfully")));
        
        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testDeleteUser_NotAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden());
        
        verify(userService, never()).deleteUser(anyLong());
    }
}
