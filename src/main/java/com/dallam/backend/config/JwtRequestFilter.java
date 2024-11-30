package com.dallam.backend.config;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dallam.backend.util.formatter.ErrorCode;
import com.dallam.backend.util.jwt.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

    	String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 필터를 적용하지 않을 기능들 (URL과 메소드 기준으로 구별)
        if (requestURI.equals("/auths/signup") && method.equals("POST") ||
        	requestURI.equals("/auths/signin") && method.equals("POST")) {
            chain.doFilter(request, response); // POST /auths/signup 요청은 필터 무시
            return;
        }
        if (requestURI.equals("/gatherings") && method.equals("GET") ||
        	requestURI.equals("/gatherings/{id}") && method.equals("GET") ||
        	requestURI.equals("/gatherings/{id}/participants") && method.equals("GET") ||
        	requestURI.equals("/reviews") && method.equals("GET") ||
        	requestURI.equals("/reviews/scores") && method.equals("GET")) {
            chain.doFilter(request, response); // POST /auths/signin 요청은 필터 무시
            return;
        }

        // Authorization 헤더에서 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더가 존재하고 Bearer로 시작하면
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
            	// 블랙리스트 확인
            	if (jwtUtil.isTokenBlacklisted(token)) {
            		sendJsonErrorResponse(response, ErrorCode.UNAUTHORIZED, "로그아웃된 사용자입니다.");
            		return;
            	}

            	// JWT 토큰에서 사용자명 추출
            	String username = jwtUtil.extractUsername(token);

            	// 사용자명이 있고 SecurityContext에 인증 정보가 없으면
            	if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            		// JWT 토큰이 유효하면 인증 처리
            		if (jwtUtil.validateToken(token, username)) {
            			// 유효한 JWT 토큰이라면 인증 정보 설정
            			UsernamePasswordAuthenticationToken authentication =
            				new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            			SecurityContextHolder.getContext().setAuthentication(authentication);
            		} else {
            			sendJsonErrorResponse(response, ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                        return;
            		}
            	}

            } catch (Exception e) {
            	sendJsonErrorResponse(response, ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                return;
            }
        }else {
        	sendJsonErrorResponse(response, ErrorCode.UNAUTHORIZED, "Authorization 헤더가 없습니다.");
            return;
        }

        // JWT 토큰이 없거나 유효하지 않으면 인증 처리 없이 그대로 필터 체인 통과
        chain.doFilter(request, response);
    }

    private void sendJsonErrorResponse(HttpServletResponse response, ErrorCode errorCode, String message) throws IOException {
    	response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json; charset=UTF-8");

        // JSON 응답 생성
        String jsonResponse = "{ \"code\": \"" + errorCode.getCode() + "\", \"message\": \"" + message + "\" }";
        response.getWriter().write(jsonResponse);
    }
}
