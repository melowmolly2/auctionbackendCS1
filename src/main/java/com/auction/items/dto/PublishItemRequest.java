package com.auction.items.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PublishItemRequest(
                @NotBlank String sellerUsername,
                @NotBlank String title,
                @NotNull OffsetDateTime endTime,
                @Positive @NotNull Double startingPrice,
                @Positive @NotNull Double buyItNowPrice,
                @Positive @NotNull Double bitIncrement) {
}