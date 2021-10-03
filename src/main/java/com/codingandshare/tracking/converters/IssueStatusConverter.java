package com.codingandshare.tracking.converters;

import com.codingandshare.tracking.domains.IssueStatus;
import com.codingandshare.tracking.exceptions.EnumValidationException;
import org.springframework.core.convert.converter.Converter;

/**
 * The custom class to convert string to {@link IssueStatus} enum
 *
 * @author Nhan Dinh
 * @since 10/2/21
 **/
public class IssueStatusConverter implements Converter<String, IssueStatus> {

  @Override
  public IssueStatus convert(String source) {
    try {
      return IssueStatus.valueOf(source);
    } catch (IllegalArgumentException ignored) {
      throw new EnumValidationException("Issue Status is invalid");
    }
  }
}
