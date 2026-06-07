package com.auction.users.dto;

/**
 * DTO đại diện cho thông tin hồ sơ công khai của người dùng.
 */
public class UserResponse {

  private final String username;
  private final String displayName;
  private final Double balance;

  public UserResponse(String username, String displayName, double balance) {
    this.username = username;
    this.displayName = displayName;
    this.balance = balance;
  }

  public String getUsername() {
    return username;
  }

  public String getDisplayName() {
    return displayName;
  }

  public double getBalance() {
    return balance;
  }
}
