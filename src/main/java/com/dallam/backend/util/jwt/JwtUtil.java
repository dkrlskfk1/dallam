package com.dallam.backend.util.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = Long.MAX_VALUE / 1000; // 토큰 유효시간

    // 블랙리스트를 저장하는 Set
    private Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    // 토큰 생성
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    // 토큰 생성 메소드
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 토큰에서 사용자 이름 추출
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 토큰의 클레임 추출
    private Claims extractAllClaims(String token) {
    	return Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY) // SecretKey로 서명 키 설정
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // 토큰 유효성 검사
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

 // 블랙리스트 추가
    public void addToBlacklist(String token) {
    	String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        blacklistedTokens.add(actualToken);
    }

    // 블랙리스트 확인
    public boolean isTokenBlacklisted(String token) {
    	String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return blacklistedTokens.contains(actualToken);
    }

}
