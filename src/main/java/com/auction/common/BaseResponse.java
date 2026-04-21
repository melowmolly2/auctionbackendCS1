package com.auction.common;

import java.util.Optional;

import jakarta.validation.constraints.NotBlank;

public class BaseResponse {
    @NotBlank
    private boolean status;

    private String message;

    private Optional item;

    public boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public BaseResponse() {
    }; // Empty Constructor so that Jackson can create and use the set methods to
       // inject data in

    public BaseResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setItem(Optional item) {
        this.item = item;
    }
}
