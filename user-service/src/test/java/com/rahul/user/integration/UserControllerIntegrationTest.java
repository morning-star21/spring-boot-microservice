package com.rahul.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.user.dto.UserDto;
import com.rahul.user.model.User;
import com.rahul.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setup() {
        // Clear database
        userRepository.deleteAll();
        
        // Create test user
        testUser = User.builder()
                .username("integrationuser")
                .email("integration@example.com")
                .firstName("Integration")
                .lastName("User")
                .phoneNumber("1234567890")
                .address("123 Integration St")
                .build();
        
        userRepository.save(testUser);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Users retrieved successfully")))
                .andExpect(jsonPath("$.data[0].username", is("integrationuser")));
    }

    @Test
    @WithMockUser(username = "integrationuser")
    public void testGetUserById_Self() throws Exception {
        mockMvc.perform(get("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("User retrieved successfully")))
                .andExpect(jsonPath("$.data.username", is("integrationuser")))
                .andExpect(jsonPath("$.data.email", is("integration@example.com")));
    }

    @Test
    @WithMockUser(username = "integrationuser")
    public void testUpdateUser_Self() throws Exception {
        UserDto updateDto = UserDto.builder()
                .username("integrationuser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .phoneNumber("9876543210")
                .address("456 Updated Ave")
                .build();
        
        mockMvc.perform(put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("User updated successfully")))
                .andExpect(jsonPath("$.data.firstName", is("Updated")))
                .andExpect(jsonPath("$.data.phoneNumber", is("9876543210")));
    }

    @Test
    public void testGetPublicUserByUsername() throws Exception {
        mockMvc.perform(get("/api/users/public/username/integrationuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("User retrieved successfully")))
                .andExpect(jsonPath("$.data.username", is("integrationuser")))
                .andExpect(jsonPath("$.data.email", is("integration@example.com")));
    }
}
