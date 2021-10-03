package com.codingandshare.tracking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Define response for user
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Builder
@Getter
public class UserInfo {

  private Integer id;

  private String username;

  private String firstName;

  private String lastName;
}
