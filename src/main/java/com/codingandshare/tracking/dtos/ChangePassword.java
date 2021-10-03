package com.codingandshare.tracking.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Define request body for change password
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Data
public class ChangePassword {

  @NotBlank(message = "oldPassword is missing")
  private String oldPassword;

  @NotBlank(message = "newPassword is missing")
  private String newPassword;
}
