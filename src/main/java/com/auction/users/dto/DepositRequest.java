package com.auction.users.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/* 
public class DepositRequest {
    Double amount;

    public DepositRequest(Double amount) {
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

} */
public record DepositRequest(@Positive @NotNull Double amount) {
}
