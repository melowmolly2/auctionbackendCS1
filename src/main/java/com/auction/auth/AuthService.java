package com.auction.auth;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auction.auth.jwtools.JwtUtil;
import com.auction.common.BaseException;
import com.auction.users.dto.AuthResponse;

import jakarta.transaction.Transactional;

@Service
public class AuthService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    @Value("${jwt.refreshExpiration}")
    private Long refreshLifetime;

    public AuthService(RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse refreshingToken(String refreshToken) {
        System.err.println(refreshToken);
        RefreshToken token = refreshTokenRepository.findRefreshTokenData(refreshToken)
                .orElseThrow(() -> new BaseException("invalid refresh token"));
        if (token.getCreatedAt() + refreshLifetime < Instant.now().toEpochMilli()) {
            throw new BaseException("refresh token has expired");
        }
        String newRefreshToken = jwtUtil.generateRefreshToken(token.getUsername());
        token.setRefreshToken(newRefreshToken);
        String accessToken = jwtUtil.generateToken(token.getUsername());
        refreshTokenRepository.save(token);
        AuthResponse response = new AuthResponse(true, "successfully refresh token", accessToken, newRefreshToken);
        return response;
    }
}
