package com.codingandshare.tracking.exceptions;

/**
 * Capture all cases when not found resource
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
public class NotFoundException extends RuntimeException {

  public NotFoundException(String message) {
    super(message);
  }
}
