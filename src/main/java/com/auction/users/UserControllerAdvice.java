package com.auction.users;

import com.auction.common.BaseResponse;
import com.auction.items.exceptions.ItemException;
import com.auction.users.exceptions.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.auction.users") // Error handling safety net
public class UserControllerAdvice {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<BaseResponse> handleNoSellerException(ItemException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.response);
    }
}
