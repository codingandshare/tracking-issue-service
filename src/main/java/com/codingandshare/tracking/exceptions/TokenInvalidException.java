package com.codingandshare.tracking.exceptions;

/**
 * The exception is capture all problem relate to token
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
public class TokenInvalidException extends Exception {

  public TokenInvalidException(String message, Throwable e) {
    super(message, e);
  }
}
