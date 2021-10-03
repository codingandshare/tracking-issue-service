package com.codingandshare.tracking.domains;

import com.codingandshare.tracking.exceptions.EnumValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Enum value for user status table
 *
 * @author Nhan Dinh
 * @since 9/17/21
 **/
public enum UserStatus {
  ACTIVE(0), INACTIVE(1);
  final int value;

  UserStatus(int value) {
    this.value = value;
  }

  /**
   * Create enum with value
   *
   * @param status
   * @return {@link UserStatus}
   * @throws EnumValidationException
   */
  @JsonCreator
  static UserStatus of(int status) throws EnumValidationException {
    for (UserStatus enumValue : values()) {
      if (enumValue.value == status) {
        return enumValue;
      }
    }
    throw new EnumValidationException("User status is invalid");
  }
}
