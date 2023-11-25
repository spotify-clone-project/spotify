package com.clone.spotify.controller;

import com.clone.spotify.entity.User;
import com.clone.spotify.service.JwtTokenProvider;
import com.clone.spotify.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
public class ApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ApiController apiController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(apiController).build();
    }

    @Test
    public void testLogin() throws Exception {
        String username = "test@example.com";
        String password = "password";
        String jsonRequest = "{\"email\":\"" + username + "\", \"password\":\"" + password + "\"}";

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(jwtTokenProvider.createToken(anyString(), any())).thenReturn("token");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    public void testSignup() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setBirth(Date.valueOf("2005-12-30"));
        user.setName("Test Name");
        user.setGender("M");

        when(userService.createUser(any(User.class))).thenReturn(user);

        String jsonRequest = "{\"email\":\"" + user.getEmail() +
                "\", \"password\":\"" + user.getPassword() +
                "\", \"birth\":\"" + user.getBirth() +
                "\", \"name\":\"" + user.getName() +
                "\", \"gender\":\"" + user.getGender() + "\"}";


        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }
}
