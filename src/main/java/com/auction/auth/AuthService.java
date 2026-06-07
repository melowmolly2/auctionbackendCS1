package com.auction.auth;

import com.auction.auth.dto.AuthResponse;
import com.auction.auth.dto.LoginRequest;
import com.auction.auth.dto.RegisterRequest;
import com.auction.auth.jwtools.JwtUtil;
import com.auction.auth.jwtools.UserDetailsImpl;
import com.auction.common.BaseException;
import com.auction.common.BaseResponse;
import com.auction.users.User;
import com.auction.users.UserService;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling user registration, login, logout, and token renewal operations.
 */
@Service
public class AuthService {
  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtUtil jwtUtil;

  // Lifetime of the Refresh Token (configured in application.properties)
  @Value("${jwt.refreshExpiration}")
  private Long refreshLifetime;

  private final PasswordEncoder passwordEncoder;
  private final UserService userService;

  // Hash string to identify banned users
  @Value("${ban_hash}")
  private String banHash;

  public AuthService(
      RefreshTokenRepository refreshTokenRepository,
      JwtUtil jwtUtil,
      PasswordEncoder passwordEncoder,
      UserService userService) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.jwtUtil = jwtUtil;
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
  }

  /**
   * Refreshes the access token when it expires.
   *
   * @param refreshToken The user's current refresh token
   * @return AuthResponse containing a new access token and a new refresh token
   */
  @Transactional
  public AuthResponse refreshingToken(String refreshToken) {
    // Find refresh token information in the DB
    RefreshToken token =
        refreshTokenRepository
            .findRefreshTokenData(refreshToken)
            .orElseThrow(() -> new BaseException("invalid refresh token"));

    // Check if the refresh token has expired
    boolean isTokenExpired = token.getCreatedAt() + refreshLifetime < Instant.now().toEpochMilli();
    if (isTokenExpired) {
      throw new BaseException("Refresh token has expired, please login again");
    }

    // Create a new token and update it in the DB
    String newRefreshTokenKey = jwtUtil.generateRefreshToken(token.getUsername());
    Long currentTime = Instant.now().toEpochMilli();
    RefreshToken newRefreshToken =
        new RefreshToken(token.getUsername(), newRefreshTokenKey, currentTime);
    String accessToken = jwtUtil.generateToken(token.getUsername());
    refreshTokenRepository.save(newRefreshToken);

    return new AuthResponse(true, "successfully refresh token", accessToken, newRefreshTokenKey);
  }

  /**
   * Registers a new user account.
   *
   * @param request Registration request containing username, displayName, and password
   * @return BaseResponse indicating the status
   */
  @Transactional
  public BaseResponse userRegister(RegisterRequest request) {
    // Check if the username has already been registered
    if (userService.existsUsername(request.username())) {
      throw new BaseException("Username has already been taken");
    }

    // Encrypt the password before saving it to the database
    String hashedPassword = passwordEncoder.encode(request.password());

    // Create a new user with a default wallet balance of 0.0
    User user = new User(request.username(), request.displayName(), hashedPassword, 0.0);
    userService.saveUser(user);
    log.info("User registered: {}", request.username());
    return new BaseResponse(true, "Successfully registered.");
  }

  /**
   * Authenticates and logs in a user account.
   *
   * @param request Login request containing username and password
   * @return AuthResponse containing successful login information with an access token and a refresh token
   */
  @Transactional
  public AuthResponse loginUser(LoginRequest request) {
    User user = userService.getUserByUsername(request.username());

    // Check if the user is banned
    if (user.getHashedPassword().equals(banHash)) {
      log.warn("Banned user attempted login: {}", request.username());
      throw new BaseException("User was banned");
    }

    if (!passwordEncoder.matches(request.password(), user.getHashedPassword())) {
      log.warn("Failed login attempt for user: {}", request.username());
      throw new BaseException("Invalid username or password");
    }

    String accessToken = jwtUtil.generateToken(user.getUsername());
    String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
    Long currentTime = Instant.now().toEpochMilli();
    refreshTokenRepository.save(new RefreshToken(user.getUsername(), refreshToken, currentTime));

    log.info("User logged in: {}", user.getUsername());
    return new AuthResponse(true, "Successfully logged in.", accessToken, refreshToken);
  }

  /**
   * Revokes/deletes a user's refresh token from the DB upon logout or account deactivation.
   *
   * @param username The username of the user whose token is to be revoked
   */
  @Transactional
  public void revokeToken(String username) {
    refreshTokenRepository.deleteById(username);
  }

  /**
   * Logs out the account and revokes the current token.
   *
   * @param userDetailsImpl Information of the currently logged-in user
   * @return BaseResponse indicating the logout status
   */
  @Transactional
  public BaseResponse logoutUser(UserDetailsImpl userDetailsImpl) {
    String username = userDetailsImpl.getUsername();
    revokeToken(username);
    log.info("User logged out: {}", username);
    return new BaseResponse(true, "successfully logout");
  }
}
