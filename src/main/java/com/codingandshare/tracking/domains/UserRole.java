package com.codingandshare.tracking.domains;

/**
 * Define all role for web application
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
public enum UserRole {

  ADMIN("ROLE_ADMIN"), USER("ROLE_USER");
  public final String value;

  UserRole(String value) {
    this.value = value;
  }
}
