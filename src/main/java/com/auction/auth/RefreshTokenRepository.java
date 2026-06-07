package com.auction.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository providing database manipulation methods for the RefreshToken entity.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

  /**
   * Searches for Refresh Token information based on the token string value.
   *
   * @param token The Refresh Token string to find
   * @return An Optional containing the RefreshToken information if found
   */
  @Query(value = "SELECT t FROM RefreshToken t WHERE t.refreshToken = :token")
  Optional<RefreshToken> findRefreshTokenData(@Param("token") String token);
}
