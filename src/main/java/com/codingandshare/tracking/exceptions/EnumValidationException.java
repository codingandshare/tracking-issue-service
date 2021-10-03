package com.codingandshare.tracking.exceptions;

/**
 * Capture exception when enum value is invalid
 *
 * @author Nhan Dinh
 * @since 9/17/21
 **/
public class EnumValidationException extends RuntimeException {

  public EnumValidationException(String message) {
    super(message);
  }
}
