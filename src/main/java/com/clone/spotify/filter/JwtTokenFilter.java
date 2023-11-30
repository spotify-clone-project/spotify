package com.clone.spotify.filter;

import com.clone.spotify.Exception.UserNotFoundException;
import com.clone.spotify.service.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 정적 리소스에 대한 요청은 토큰 검증을 건너뜁니다.
        if (isStaticResource(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 엑세스 토큰을 가져옴
            String accessToken = extractToken(request);

            // 토큰이 유효하다면 시큐리티 객체에 유저정보를 넣음 (로그인과 동일)
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                System.out.println("인증 성공!");
                Authentication auth = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            else if(accessToken != null && jwtTokenProvider.validateRefreshToken(accessToken)) {
                filterChain.doFilter(request, response);
            }
            else {
                // 유효하지 않은 토큰일 경우 401 상태 코드를 설정
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (UserNotFoundException e) {
            log.error("UserNotFoundException: {}", e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    // 정적 리소스 및 지정된 경로 확인 메서드
    private boolean isStaticResource(String uri) {
        return uri.startsWith("/images/") || uri.startsWith("/js/") ||
                uri.startsWith("/css/") || uri.startsWith("/api/auth/") || uri.startsWith("/file/music/") ||
                uri.equals("/signup") || uri.equals("/login");
    }
}