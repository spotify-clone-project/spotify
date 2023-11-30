package com.clone.spotify.controller;

import com.clone.spotify.entity.User;
import com.clone.spotify.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody User data) {
        try {
            Map<String, String> tokens = authenticationService.authenticateUserAndCreateTokens(data);
            return ResponseEntity.ok().body(tokens);
        } catch (AuthenticationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "이메일 또는 비밀번호가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        String originalPassword = user.getPassword();  // 원본 비밀번호 저장
        authenticationService.createUser(user);  // DB에 유저 저장 (비밀번호 암호화)

        // 원본 비밀번호를 가진 새로운 User 객체 생성
        User userForAuth = new User();
        userForAuth.setEmail(user.getEmail());
        userForAuth.setPassword(originalPassword);

        // 인증 및 토큰 생성
        Map<String, String> tokens = authenticationService.authenticateUserAndCreateTokens(userForAuth);

        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authenticationService.logout(request);
        return ResponseEntity.ok().body("Logout successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return authenticationService.refreshToken(request);
    }



}
