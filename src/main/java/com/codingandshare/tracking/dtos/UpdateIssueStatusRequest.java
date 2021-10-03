package com.codingandshare.tracking.dtos;

import com.codingandshare.tracking.domains.IssueStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Define request body for update status of issue
 *
 * @author Nhan Dinh
 * @since 10/2/21
 **/
@Data
public class UpdateIssueStatusRequest {

  @Size(max = 255, message = "Maximum 255 characters")
  private String changedNote;

  @NotNull(message = "status is missing")
  private IssueStatus status;
}
