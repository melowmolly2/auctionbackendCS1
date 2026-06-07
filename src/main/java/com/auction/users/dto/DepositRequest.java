package com.auction.users.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO yêu cầu nạp tiền vào số dư của người dùng.
 */
public record DepositRequest(@Positive @NotNull Double amount) {}
