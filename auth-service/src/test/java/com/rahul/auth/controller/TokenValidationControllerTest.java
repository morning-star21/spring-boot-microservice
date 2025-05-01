package com.rahul.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.auth.dto.TokenValidationRequest;
import com.rahul.auth.security.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TokenValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    public void testValidateToken_ValidToken() throws Exception {
        // Arrange
        TokenValidationRequest request = new TokenValidationRequest();
        request.setToken("valid_token");
        
        when(jwtUtils.validateJwtToken("valid_token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid_token")).thenReturn("testuser");
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/token/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid", is(true)))
                .andExpect(jsonPath("$.data.username", is("testuser")));
    }

    @Test
    public void testValidateToken_InvalidToken() throws Exception {
        // Arrange
        TokenValidationRequest request = new TokenValidationRequest();
        request.setToken("invalid_token");
        
        when(jwtUtils.validateJwtToken("invalid_token")).thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/token/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid", is(false)))
                .andExpect(jsonPath("$.data.username").doesNotExist());
    }
}
