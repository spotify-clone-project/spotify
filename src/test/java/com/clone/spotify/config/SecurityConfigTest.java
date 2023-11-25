package com.clone.spotify.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private WebSecurityConfigurerAdapter securityConfig;

    @Test
    public void contextLoads() {
        assertNotNull(securityConfig, "Security configuration should be loaded");
    }

}
