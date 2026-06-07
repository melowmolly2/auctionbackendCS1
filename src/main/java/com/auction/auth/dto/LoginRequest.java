package com.auction.auth.dto;

import com.auction.common.annotations.NoSpace;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Record representing the user's login request data.
 */
public record LoginRequest(
    // Login username (not empty, not null, no spaces)
    @NotEmpty(message = "Username must not be empty")
        @NotNull(message = "Username must not be null")
        @NoSpace(message = "Username can't have space")
        String username,

    // Login password (not empty, not null, no spaces)
    @NotEmpty(message = "Password must not be empty")
        @NotNull(message = "Password must not be null")
        @NoSpace(message = "Password can't have space")
        String password) {}
