package com.clone.spotify.service;

import com.clone.spotify.Exception.UserNotFoundException;
import io.jsonwebtoken.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtTokenProvider {

    private final UserService userService;

    public JwtTokenProvider(UserService userService) {
        this.userService = userService;
    }

    @Value("${jwt.accessSecret}")
    private String secretKey;

    @Getter
    @Value("${jwt.accessExpiration}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refreshSecret}")
    private String refreshSecretKey;

    @Getter
    @Value("${jwt.refreshExpiration}")
    private long refreshTokenValidityInMilliseconds;


    public String createToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds); // 리프레시 토큰의 유효기간 설정

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey) // 리프레시 토큰용 비밀키 사용
                .compact();
    }

    // 리프레시 토큰 유효성 검증
    public boolean validateRefreshToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        // 토큰에서 사용자 이름 추출
        String email = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();

        UserDetails userDetails = userService.loadUserByUsername(email);
        if (userDetails == null) {
            throw new UserNotFoundException("User not found with username: " + email);
        }
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token).getBody().getSubject();
    }


    public long getRemainingTime(String refreshToken) {
        // 토큰에서 만료 시간을 추출
        Date expiration = Jwts.parser()
                .setSigningKey(refreshSecretKey) // secretKey는 토큰을 검증하는데 사용하는 키입니다
                .parseClaimsJws(refreshToken)
                .getBody()
                .getExpiration();

        // 현재 시간과의 차이를 계산 (밀리초 단위)
        long remainingTime = expiration.getTime() - System.currentTimeMillis();

        // 밀리초를 초 단위로 변환하여 반환
        return remainingTime / 1000;
    }
}

