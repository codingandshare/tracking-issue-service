package com.codingandshare.tracking.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Define request body for login endpoint
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Data
public class AuthenRequest {

  @NotBlank(message = "username is missing")
  private String username;

  @NotBlank(message = "password is missing")
  private String password;
}
