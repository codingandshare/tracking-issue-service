package com.codingandshare.tracking.converters;

import com.codingandshare.tracking.domains.UserStatus;
import com.codingandshare.tracking.exceptions.EnumValidationException;
import org.springframework.core.convert.converter.Converter;

/**
 * The custom class to convert string to {@link UserStatus} enum
 *
 * @author Nhan Dinh
 * @since 10/2/21
 **/
public class UserStatusConverter implements Converter<String, UserStatus> {

  @Override
  public UserStatus convert(String source) {
    try {
      return UserStatus.valueOf(source);
    } catch (IllegalArgumentException ignored) {
      throw new EnumValidationException("User Status is invalid");
    }
  }
}
