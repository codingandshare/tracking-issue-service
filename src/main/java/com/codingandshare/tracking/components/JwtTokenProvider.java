package com.codingandshare.tracking.components;

import com.codingandshare.tracking.exceptions.TokenInvalidException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * The class is component spring
 * Provider business relate to token
 * - Verify token
 * - Generate new token
 * - Parse token
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Component
public class JwtTokenProvider {

  private final static long JWT_EXPIRATION = 604800000L;

  @Value("${app.jwtSecret}")
  private String jwtSecret;

  /**
   * Generate token from username
   * with token expire time {@link #JWT_EXPIRATION}
   *
   * @param username need to generate token
   * @return token string
   */
  public String generateToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
    JwtBuilder jwtBuilder = Jwts.builder();
    jwtBuilder.setSubject(username);
    jwtBuilder.setIssuedAt(now);
    jwtBuilder.setExpiration(expiryDate);
    return jwtBuilder
        .signWith(SignatureAlgorithm.HS512, this.jwtSecret)
        .compact();
  }

  /**
   * Get username from string token
   * When the token is valid will got username hash on that token
   * When the token is invalid will throw exception error
   *
   * @param token input token
   * @return username
   */
  public String getUsernameFromToken(String token) {
    JwtParser jwtParser = Jwts.parser();
    jwtParser.setSigningKey(this.jwtSecret);
    Claims claims = jwtParser.parseClaimsJws(token).getBody();
    return claims.getSubject();
  }

  /**
   * Validate token is valid or invalid
   *
   * @param token need to validate
   * @throws TokenInvalidException when token is invalid
   */
  public void validateToken(String token) throws TokenInvalidException {
    try {
      JwtParser jwtParser = Jwts.parser();
      jwtParser.setSigningKey(this.jwtSecret);
      jwtParser.parseClaimsJws(token);
    } catch (SignatureException | MalformedJwtException |
        ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
      throw new TokenInvalidException("Token is invalid", e);
    }
  }
}
