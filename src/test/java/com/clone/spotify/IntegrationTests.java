package com.clone.spotify;

import com.clone.spotify.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IntegrationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void testUserSignupLoginAndLogout() throws Exception {
        MockMvc mockMvc = webAppContextSetup(webApplicationContext).build();

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setBirth(Date.valueOf("2005-12-30"));
        user.setName("Test Name");
        user.setGender("Man");

        // 회원가입 테스트
        mockMvc.perform(post("/api/signup")
                        .contentType("application/json")
                        .content("{\"email\":\"" + user.getEmail() +
                                "\", \"password\":\"" + user.getPassword() +
                                "\", \"birth\":\"" + user.getBirth() +
                                "\", \"name\":\"" + user.getName() +
                                "\", \"gender\":\"" + user.getGender() + "\"}"))
                .andExpect(status().isCreated());

        // 로그인 테스트
        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content("{\"email\":\"" + user.getEmail() +
                                "\", \"password\":\"" + user.getPassword() + "\"}"))
                .andExpect(status().isOk());

        // 로그아웃 테스트
        mockMvc.perform(post("/api/logout"))
                .andExpect(status().isOk());
    }
}

