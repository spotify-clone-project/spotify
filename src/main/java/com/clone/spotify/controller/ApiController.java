package com.clone.spotify.controller;

import com.clone.spotify.entity.User;
import com.clone.spotify.service.JwtTokenProvider;
import com.clone.spotify.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    public ApiController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody User data, HttpServletResponse response) {
        try {

            String username = data.getEmail();
            String password = data.getPassword();

            // 서비스 레이어 호출
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.createToken(username, authentication.getAuthorities());

            // 쿠키 생성 및 설정
            Cookie cookie = new Cookie("JWT_TOKEN", token);
            cookie.setHttpOnly(true); // XSS 방지
            cookie.setPath("/"); // 쿠키 경로 설정
            cookie.setMaxAge((int) (validityInMilliseconds / 1000)); // 쿠키의 만료 시간을 초단위로 설정
            response.addCookie(cookie);

            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);

            return ResponseEntity.ok(model);

        } catch (AuthenticationException e) {
            // 로그인 실패 시 반환할 메시지
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "이메일 및 비밀번호 조합이 잘못되었습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // 사용자에게 받은 정보로 유저를 생성해서
        User newUser = userService.createUser(user);
        // 생성한 유저를 response에 담아 반환해준다 (로그인을 바로 시키기 위함)
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        userService.logout(response);
        return ResponseEntity.ok().body("Logout successful");
    }

}
