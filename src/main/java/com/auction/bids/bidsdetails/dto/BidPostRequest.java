package com.auction.bids.bidsdetails.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BidPostRequest(
        @NotNull Long itemId, @NotBlank String username, @Positive @NotNull Long bidAmount) {
}