package com.codingandshare.tracking.services;

import com.codingandshare.tracking.components.JwtTokenProvider;
import com.codingandshare.tracking.domains.User;
import com.codingandshare.tracking.domains.UserDetailsImpl;
import com.codingandshare.tracking.dtos.AuthenRequest;
import com.codingandshare.tracking.dtos.TokenResponse;
import com.codingandshare.tracking.exceptions.NotFoundException;
import com.codingandshare.tracking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The class provide for config security spring
 * Implement the interface {@link UserDetailsService}
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Service
public class AuthenUserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  /**
   * Implement method support for config spring security
   *
   * @param username need to authen
   * @return {@link UserDetails}
   * @throws UsernameNotFoundException when user name not found in database
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> userOptional = this.userRepository.findUserByUsername(username);
    User user = userOptional.orElseThrow(() ->
        new UsernameNotFoundException(String.format("User %s not found", username))
    );
    return new UserDetailsImpl(user);
  }

  /**
   * Handler check authentication from spring security
   * if success will return generate new token
   * if failed will response status 401
   *
   * @param authenRequest contain username and password
   * @return {@link TokenResponse}
   */
  public TokenResponse login(AuthenRequest authenRequest) {
    Authentication authentication = this.authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            authenRequest.getUsername(),
            authenRequest.getPassword()
        )
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String token = this.jwtTokenProvider.generateToken(userDetails.getUsername());
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setToken(token);
    return tokenResponse;
  }

  /**
   * Get user info current login
   *
   * @return {@link User}
   */
  public User getUserCurrentLogin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof UsernamePasswordAuthenticationToken) {
      UserDetailsImpl customUserDetails = (UserDetailsImpl) authentication.getPrincipal();
      return customUserDetails.getUser();
    } else {
      throw new NotFoundException("User not found");
    }
  }
}
