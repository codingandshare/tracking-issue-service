package com.codingandshare.tracking.dtos;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Nhan Dinh
 * @since 10/2/21
 **/
@Data
public class UserUpdateRequest {

  @NotBlank(message = "firstName is missing")
  private String firstName;

  @NotBlank(message = "lastName is missing")
  private String lastName;

  @Email(message = "Email is invalid")
  @NotBlank(message = "email is missing")
  private String email;

  @NotNull(message = "gender is missing")
  private Integer gender;
}
