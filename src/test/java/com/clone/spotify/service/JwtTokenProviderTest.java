package com.clone.spotify.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtTokenProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @Test
    public void testCreateAndValidateToken() {
        String username = "test@example.com";
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        User user = new User(username, "", Collections.singletonList(authority));

        String token = jwtTokenProvider.createToken(username, user.getAuthorities());
        assertNotNull(token);

        boolean isValid = jwtTokenProvider.validateToken(token);
        assertTrue(isValid);
    }
}
