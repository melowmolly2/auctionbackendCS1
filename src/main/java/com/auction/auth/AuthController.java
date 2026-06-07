package com.auction.auth;

import com.auction.auth.dto.AuthResponse;
import com.auction.auth.dto.LoginRequest;
import com.auction.auth.dto.RefreshTokenRequest;
import com.auction.auth.dto.RegisterRequest;
import com.auction.auth.jwtools.UserDetailsImpl;
import com.auction.common.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for providing APIs related to Authentication and Authorization
 * such as Register, Login, Logout, and Refresh Token.
 */
@RestController
@RequestMapping("")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * API to refresh the Access Token using a Refresh Token.
   * POST /refresh
   *
   * @param request Request containing the Refresh Token
   * @return ResponseEntity containing a new token pair (Access Token and Refresh Token)
   */
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    AuthResponse authResponse = authService.refreshingToken(request.refreshToken());
    return ResponseEntity.ok().body(authResponse);
  }

  /**
   * API for user login to the system.
   * POST /login
   *
   * @param request Login request containing username and password
   * @return ResponseEntity containing an authentication token if the login information is correct
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = authService.loginUser(request);
    return ResponseEntity.ok().body(response);
  }

  /**
   * API for registering a new user account.
   * POST /register
   *
   * @param request Registration request containing username, displayName, and password
   * @return ResponseEntity responding with the successful registration status
   */
  @PostMapping("/register")
  public ResponseEntity<BaseResponse> register(@Valid @RequestBody RegisterRequest request) {
    BaseResponse response = authService.userRegister(request);
    return ResponseEntity.ok().body(response);
  }

  /**
   * API for logging out of the system, revoking the current Refresh Token.
   * POST /logout
   *
   * @param userDetailsImpl Current user information obtained from the Security Context
   * @return ResponseEntity responding with the successful logout status
   */
  @PostMapping("/logout")
  public ResponseEntity<BaseResponse> logout(
      @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
    BaseResponse response = authService.logoutUser(userDetailsImpl);
    return ResponseEntity.ok(response);
  }
}
