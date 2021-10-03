package com.codingandshare.tracking.components;

import com.codingandshare.tracking.exceptions.TokenInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The class handle filter per request
 * Checking and add authentication profile to request for spring security handler
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  @Qualifier("authenUserService")
  private UserDetailsService userDetailsService;

  /**
   * Validate token
   * Parse token to get info user profile for spring security handler
   *
   * @param request     http request
   * @param response    http response
   * @param filterChain filter main
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    try {
      String token = getJwtFromRequest(request);
      if (StringUtils.hasText(token)) {
        this.jwtTokenProvider.validateToken(token);
        String userName = this.jwtTokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (TokenInvalidException e) {
      log.error("Validate token failed", e);
    } catch (UsernameNotFoundException e) {
      log.error("User name not found", e);
    } finally {
      filterChain.doFilter(request, response);
    }
  }

  /**
   * Get token from request header
   *
   * @param request
   * @return token
   */
  private static String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
