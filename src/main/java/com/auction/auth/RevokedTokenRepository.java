package com.auction.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for database operations with the RevokedToken entity.
 */
@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {}
