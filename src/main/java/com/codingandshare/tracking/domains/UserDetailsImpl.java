package com.codingandshare.tracking.domains;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * The class implement interface {@link UserDetails}
 * For config authentication spring security
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
public class UserDetailsImpl implements UserDetails {
  private final User user;

  public UserDetailsImpl(User user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.user.getRoles().stream()
        .map(it -> new SimpleGrantedAuthority(it.getRoleName()))
        .collect(Collectors.toList());
  }

  @Override
  public String getPassword() {
    return this.user.getPassword();
  }

  @Override
  public String getUsername() {
    return this.user.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.user.getStatus() == UserStatus.ACTIVE;
  }

  public User getUser() {
    return user;
  }
}
