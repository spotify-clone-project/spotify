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

    public Map<String, String> authenticateUserAndCreateTokens(User user) {
        String username = user.getEmail();
        String password = user.getPassword();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createToken(username, authentication.getAuthorities());
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
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

    public void logout() {

        System.out.println("로그아웃 로직 작성 필요");

    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = extractToken(request, "Authorization");
        if (refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

            UserDetails userDetails = userService.loadUserByUsername(username);
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            // 새로운 액세스 토큰 생성
            String newAccessToken = jwtTokenProvider.createToken(username, authorities);

            // 새로운 액세스 토큰 반환
            Map<Object, Object> model = new HashMap<>();
            model.put("accessToken", newAccessToken);
            return ResponseEntity.ok(model);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    private String extractToken(HttpServletRequest request, String headerName) {
        String bearerToken = request.getHeader(headerName);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
