package com.auction.auth.dto;

import com.auction.common.annotations.NoSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Record representing the data for a new account registration request.
 */
public record RegisterRequest(
    // Registration username (not empty, not null, no spaces)
    @NotEmpty(message = "Username must not be empty")
        @NotNull(message = "Username must not be null")
        @NoSpace(message = "Username can't have space")
        String username,

    // Public display name (not null, not blank)
    @NotNull(message = "Display name must not be null")
        @NotBlank(message = "Display name can't be blank")
        String displayName,

    // Password (not empty, not null, no spaces)
    @NotEmpty(message = "Password must not be empty")
        @NotNull(message = "Password must not be null")
        @NoSpace(message = "Password can't have space")
        String password) {}
