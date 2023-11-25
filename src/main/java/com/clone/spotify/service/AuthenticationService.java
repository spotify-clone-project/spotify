package com.clone.spotify.service;

import com.clone.spotify.entity.Role;
import com.clone.spotify.entity.User;
import com.clone.spotify.repository.RoleRepository;
import com.clone.spotify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Map<String, String> authenticateUserAndCreateTokens(User user, HttpServletResponse response) {
        String username = user.getEmail();
        String password = user.getPassword();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createToken(username, authentication.getAuthorities());
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        setTokenCookie(response, "ACCESS_TOKEN", accessToken, jwtTokenProvider.getAccessTokenValidityInMilliseconds());
        setTokenCookie(response, "REFRESH_TOKEN", refreshToken, jwtTokenProvider.getRefreshTokenValidityInMilliseconds());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        System.out.println("생성한 토큰: " + accessToken + "/" + refreshToken);

        return tokens;
    }

    private void setTokenCookie(HttpServletResponse response, String name, String token, long duration) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (duration / 1000));
        response.addCookie(cookie);
    }

    public User createUser(User user) {
        // 평문 패스워드를 불러옴
        String pass = user.getPassword();

        // 암호화 시킴
        String hashedPass = passwordEncoder.encode(pass);

        // 암호화된 패스워드를 저장함
        user.setPassword(hashedPass);

        // 역할 설정 로직
        Collection<Role> roles = new HashSet<>();
        Role defaultRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            return roleRepository.save(newRole);
        });
        roles.add(defaultRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public void logout(HttpServletResponse response) {
        // 쿠키에서 JWT 토큰 제거
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 만료
        response.addCookie(cookie);
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);
        if (refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

            UserDetails userDetails = userService.loadUserByUsername(username);
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            // 새로운 액세스 토큰 생성
            String newAccessToken = jwtTokenProvider.createToken(username, authorities);

            // 액세스 토큰 쿠키 설정
            Cookie accessCookie = new Cookie("ACCESS_TOKEN", newAccessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge((int) (jwtTokenProvider.getAccessTokenValidityInMilliseconds() / 1000));
            response.addCookie(accessCookie);

            // 새로운 액세스 토큰 반환
            Map<Object, Object> model = new HashMap<>();
            model.put("accessToken", newAccessToken);
            return ResponseEntity.ok(model);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("REFRESH_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
