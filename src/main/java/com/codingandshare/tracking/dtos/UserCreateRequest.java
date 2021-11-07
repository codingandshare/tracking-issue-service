package com.codingandshare.tracking.dtos;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Define request body for create user
 *
 * @author Nhan Dinh
 * @since 10/2/21
 **/
@Data
public class UserCreateRequest {

  @NotBlank(message = "username is missing")
  private String username;

  @NotBlank(message = "password is missing")
  private String password;

  @NotBlank(message = "firstName is missing")
  private String firstName;

  @NotBlank(message = "lastName is missing")
  private String lastName;

  @NotBlank(message = "email is missing")
  @Email(message = "email is invalid format")
  private String email;

  @NotNull(message = "gender is missing")
  private Integer gender;
}
