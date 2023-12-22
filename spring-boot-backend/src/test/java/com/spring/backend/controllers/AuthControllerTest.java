package com.spring.backend.controllers;

import com.spring.backend.payload.request.LoginRequest;
import com.spring.backend.payload.request.SignupRequest;
import com.spring.backend.payload.request.TokenRefreshRequest;
import com.spring.backend.payload.response.JwtResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;
    private String refreshToken;

    @Test
    public void testSignIn() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test", "123456");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.roles").exists());

        String content = result.andReturn().getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(content, JwtResponse.class);

        jwtToken = jwtResponse.getAccessToken();
        refreshToken = jwtResponse.getRefreshToken();

        System.out.println(jwtToken);
        System.out.println(content);
        System.out.println(refreshToken);


    }

    @Test
    public void testSignUp() throws Exception {
        SignupRequest signupRequest = new SignupRequest("newuser", "newuser@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @WithMockUser
    public void testRefreshToken() throws Exception {



        String content = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("test", "123456"))))
                .andReturn().getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(content, JwtResponse.class);
        refreshToken = jwtResponse.getRefreshToken();

        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest(refreshToken);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRefreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @WithMockUser
    public void testLogoutUser() throws Exception {
        String content = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("test", "123456"))))
                .andReturn().getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(content, JwtResponse.class);
        refreshToken = jwtResponse.getRefreshToken();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Log out successful!"));
    }
}
