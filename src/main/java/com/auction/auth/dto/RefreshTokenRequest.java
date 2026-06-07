package com.auction.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Record representing the request data for renewing an Access Token using a Refresh Token.
 */
public record RefreshTokenRequest(
    // The user's existing Refresh Token string, cannot be blank
    @NotBlank String refreshToken) {}
