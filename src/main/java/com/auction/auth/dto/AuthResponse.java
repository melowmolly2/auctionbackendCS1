package com.auction.auth.dto;

import com.auction.common.BaseResponse;

/**
 * Response class for authentication information after successful login or token refresh.
 */
public class AuthResponse extends BaseResponse {
  // Access Token used to access secure resources (usually has a short lifespan)
  private String accessToken;

  // Refresh Token used to renew the Access Token (usually has a longer lifespan)
  private String refreshToken;

  public AuthResponse(boolean status, String message, String accessToken, String refreshToken) {
    super(status, message);
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
