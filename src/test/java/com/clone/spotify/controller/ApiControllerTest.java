package com.clone.spotify.controller;

import com.clone.spotify.entity.User;
import com.clone.spotify.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
public class ApiControllerTest {

    private MockMvc mockMvc;
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        authenticationService = mock(AuthenticationService.class);
        AuthController apiController = new AuthController(authenticationService);
        mockMvc = MockMvcBuilders.standaloneSetup(apiController).build();
    }

    @Test
    public void testLogin() throws Exception {
        String username = "test@example.com";
        String password = "password";
        String jsonRequest = "{\"email\":\"" + username + "\", \"password\":\"" + password + "\"}";

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", "accessToken");
        tokens.put("refreshToken", "refreshToken");

        when(authenticationService.authenticateUserAndCreateTokens(any(User.class), any(HttpServletResponse.class)))
                .thenReturn(tokens);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    public void testSignup() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", "accessToken");
        tokens.put("refreshToken", "refreshToken");

        when(authenticationService.createUser(any(User.class))).thenReturn(user);
        when(authenticationService.authenticateUserAndCreateTokens(any(User.class), any(HttpServletResponse.class)))
                .thenReturn(tokens);

        String jsonRequest = "{\"email\":\"" + user.getEmail() + "\", \"password\":\"" + user.getPassword() + "\"}";

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }
}
