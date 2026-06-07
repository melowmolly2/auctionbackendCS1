package com.auction.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The RefreshToken entity represents a refresh token used to renew an access token.
 * It is stored in the database to manage user sessions.
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

  // The username of the user (Primary Key), each account can have at most one refresh token at a time
  @Id
  @Column(name = "username")
  private String username;

  // The value of the JWT-encoded refresh token
  @Column(name = "token")
  private String refreshToken;

  // The creation time of the refresh token (Epoch Milliseconds)
  @Column(name = "created_at")
  private Long createdAt;

  /**
   * JPA Lifecycle Callback: Automatically records the token creation time before saving to the DB.
   */
  protected RefreshToken() {}

  public RefreshToken(String username, String refreshToken, Long createdAt) {
    this.username = username;
    this.refreshToken = refreshToken;
    this.createdAt = createdAt;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
  }
}
