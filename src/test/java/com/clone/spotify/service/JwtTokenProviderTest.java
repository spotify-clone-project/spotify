package com.clone.spotify.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private UserService userService;

    @Test
    public void testCreateAndValidateToken() {
        String username = "test@example.com";
        var authority = new SimpleGrantedAuthority("ROLE_USER");
        var user = new User(username, "", Collections.singletonList(authority));

        String token = jwtTokenProvider.createToken(username, user.getAuthorities());
        assertNotNull(token);

        boolean isValid = jwtTokenProvider.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    public void testCreateAndValidateRefreshToken() {
        String username = "test@example.com";

        String refreshToken = jwtTokenProvider.createRefreshToken(username);
        assertNotNull(refreshToken);

        boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);
        assertTrue(isValid);
    }

    @Test
    public void testGetAuthentication() {
        String username = "test@example.com";
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        UserDetails mockUser = new User(username, "", Collections.singletonList(authority));

        when(userService.loadUserByUsername(username)).thenReturn(mockUser);

        String token = jwtTokenProvider.createToken(username, mockUser.getAuthorities());
        assertNotNull(token);

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
    }

    @Test
    public void testGetUsernameFromToken() {
        String username = "test@example.com";
        var authority = new SimpleGrantedAuthority("ROLE_USER");
        var user = new User(username, "", Collections.singletonList(authority));

        String token = jwtTokenProvider.createToken(username, user.getAuthorities());
        assertNotNull(token);

        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);
        assertNotNull(extractedUsername);
        assertEquals(username, extractedUsername);
    }
}
