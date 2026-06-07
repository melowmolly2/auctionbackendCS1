package com.auction.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The RevokedToken entity stores information about revoked tokens or banned users.
 * It is used to check the validity of previously issued JWTs.
 */
@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

  // The username of the user whose privileges have been revoked or who has been banned (Primary Key)
  @Id
  @Column(name = "username")
  private String username;

  // The time the user was banned or the token was revoked (Epoch Milliseconds)
  @Column(name = "banned_at", nullable = false)
  private Long bannedAt;

  protected RevokedToken() {}

  public RevokedToken(String username, Long bannedAt) {
    this.username = username;
    this.bannedAt = bannedAt;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Long getBannedAt() {
    return bannedAt;
  }

  public void setBannedAt(Long bannedAt) {
    this.bannedAt = bannedAt;
  }
}
