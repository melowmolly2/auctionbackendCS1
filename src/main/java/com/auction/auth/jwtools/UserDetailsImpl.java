package com.auction.auth.jwtools;

import com.auction.users.User;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of Spring Security's UserDetails. Used to store authenticated user account information
 * in the Spring Security Context.
 */
public class UserDetailsImpl implements UserDetails {
  private String username;
  private String displayName;
  private Double balance;
  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(
      String username,
      String displayName,
      Double balance,
      Collection<? extends GrantedAuthority> authorities) {
    this.username = username;
    this.displayName = displayName;
    this.balance = balance;
    this.authorities = authorities;
  }

  /**
   * Static helper method to convert from a User entity object (JPA) to a UserDetailsImpl object.
   * Assigns the ROLE_ADMIN role if the username is "admin".
   *
   * @param user The User entity to convert
   * @return The corresponding UserDetailsImpl object
   */
  public static UserDetailsImpl JPAtoUserDetails(User user) {
    List<GrantedAuthority> authorities;
    if ("admin".equals(user.getUsername())) {
      authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    } else {
      authorities = List.of();
    }
    return new UserDetailsImpl(
        user.getUsername(), user.getDisplayName(), user.getBalance(), authorities);
  }

  @Override
  public String getUsername() {
    return username;
  }

  // Returns an empty password because the JWT mechanism does not need to store the password in UserDetails after authentication
  @Override
  public String getPassword() {
    return "";
  }

  public String getDisplayName() {
    return displayName;
  }

  public Double getBalance() {
    return balance;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }
}
