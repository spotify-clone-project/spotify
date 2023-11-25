package com.clone.spotify.filter;

import com.clone.spotify.Exception.UserNotFoundException;
import com.clone.spotify.service.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
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

        try {
            String accessToken = extractToken(request, "ACCESS_TOKEN");
            String refreshToken = extractToken(request, "REFRESH_TOKEN");

            log.info("Extracted Access Token: {}", accessToken);
            log.info("Extracted Refresh Token: {}", refreshToken);

            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                log.info("Access Token validation successful");
                Authentication auth = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.warn("Access Token validation failed or token is null for request: {}", request.getRequestURL());
                if (refreshToken != null && !jwtTokenProvider.validateRefreshToken(refreshToken)) {
                    log.warn("Refresh Token is invalid, expiring it");
                    expireCookie(response, "REFRESH_TOKEN");
                }
            }
        } catch (UserNotFoundException e) {
            log.error("UserNotFoundException: {}", e.getMessage());
            e.printStackTrace();
            expireCookie(response, "ACCESS_TOKEN");
            expireCookie(response, "REFRESH_TOKEN");
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String extractToken(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 만료
        response.addCookie(cookie);
    }
}