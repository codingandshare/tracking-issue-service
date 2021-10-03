package com.codingandshare.tracking.domains;

import com.codingandshare.tracking.exceptions.EnumValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Define all statues for issue
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
public enum IssueStatus {
  OPEN, REOPEN, IN_PROGRESS, RESOLVED, DONE, WITHDRAW, DUPLICATED;

  @JsonCreator
  static IssueStatus of(String value) {
    try {
      return IssueStatus.valueOf(value);
    } catch (IllegalArgumentException ignored) {
      throw new EnumValidationException("Issue Status is invalid");
    }
  }
}
