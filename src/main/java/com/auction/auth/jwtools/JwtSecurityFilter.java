package com.auction.auth.jwtools;

import com.auction.auth.RevokedToken;
import com.auction.auth.RevokedTokenRepository;
import com.auction.auth.exceptions.JwtExpiredException;
import com.auction.users.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * JWT Security Filter (JwtSecurityFilter) that extends OncePerRequestFilter.
 * Ensures it is activated only once for each incoming HTTP request.
 * Validates the JWT, checks for user revocation/ban, and sets up the Security Context.
 */
@Component
public class JwtSecurityFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final RevokedTokenRepository revokedTokenRepository;
  private final UserService userService;
  private final HandlerExceptionResolver resolver;
  private final List<RequestMatcher> publicMatchers;

  public JwtSecurityFilter(
      JwtUtil jwtUtil,
      UserService userService,
      RevokedTokenRepository revokedTokenRepository,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.jwtUtil = jwtUtil;
    this.userService = userService;
    this.revokedTokenRepository = revokedTokenRepository;
    this.resolver = resolver;

    // List of public path patterns that are allowed free access without JWT checking
    this.publicMatchers =
        List.of(
            PathPatternRequestMatcher.pathPattern("/users/login"),
            PathPatternRequestMatcher.pathPattern("/swagger-ui/**"),
            PathPatternRequestMatcher.pathPattern("/swagger.json"),
            PathPatternRequestMatcher.pathPattern("/swagger-ui.html"),
            PathPatternRequestMatcher.pathPattern("/v3/api-docs/**"),
            PathPatternRequestMatcher.pathPattern("/register"),
            PathPatternRequestMatcher.pathPattern("/login"),
            PathPatternRequestMatcher.pathPattern("/refresh"),
            PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/items/**"),
            PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/item/status/**"));
  }

  /**
   * Main filtering logic, extracts and validates the JWT, loads user information into the Security Context.
   */
  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String encodedToken = parseJwt(request);

    // If no header then let Spring Security Handle.
    if (encodedToken == null) {
      filterChain.doFilter(request, response);
      return;
    }

    boolean isTokenValidated;

    try {
      isTokenValidated = jwtUtil.validateJwtToken(encodedToken);
    } catch (JwtExpiredException e) {
      resolver.resolveException(request, response, null, e);
      return;
    }

    if (isTokenValidated) {
      String username = jwtUtil.getUserFromToken(encodedToken);
      Date issuedAt = jwtUtil.getIssuedAtFromToken(encodedToken);

      // Check if this account is on the list of revoked tokens / banned users
      Optional<RevokedToken> revoked = revokedTokenRepository.findById(username);

      if (revoked.isPresent()) {
        // If the token's issuance time (issuedAt) occurred before the ban time (bannedAt) -> Do not authenticate
        if (!issuedAt.toInstant().isAfter(Instant.ofEpochMilli(revoked.get().getBannedAt()))) {
          filterChain.doFilter(request, response);
          return;
        }
        // If the issuance time is after the ban time (user has been unbanned and logged in again),
        // delete the ban record from the DB
        revokedTokenRepository.delete(revoked.get());
      }

      // Load user account information from the DB
      UserDetailsImpl userDetails =
          UserDetailsImpl.JPAtoUserDetails(userService.getUserByUsername(username));

      // Create an authentication object representing the user
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      // Save the authentication object to the Security Context of the current thread
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    // Continue the filter chain
    filterChain.doFilter(request, response);
  }

  /**
   * Determines whether the current request needs to go through the JWT filter.
   * Skips the filter if the path belongs to the publicMatchers list.
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return publicMatchers.stream().anyMatch(m -> m.matches(request));
  }

  /**
   * Extracts the JWT token from the "Authorization" header in the incoming request.
   *
   * @param request The received HTTP request
   * @return The JWT string after removing the "Bearer " prefix, or null if invalid
   */
  public String parseJwt(HttpServletRequest request) {
    String authenticationHeader = request.getHeader("Authorization");

    if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")) {
      return authenticationHeader.substring(7);
    }
    return null;
  }
}
