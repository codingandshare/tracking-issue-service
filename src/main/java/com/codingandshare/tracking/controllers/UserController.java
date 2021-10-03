package com.codingandshare.tracking.controllers;

import com.codingandshare.tracking.domains.User;
import com.codingandshare.tracking.domains.UserStatus;
import com.codingandshare.tracking.dtos.PageDTO;
import com.codingandshare.tracking.dtos.UserCreateRequest;
import com.codingandshare.tracking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Nhan Dinh
 * @since 10/2/21
 **/
@RestController
@RequestMapping(path = "/user")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void createUser(
      @RequestBody @Valid UserCreateRequest userCreateRequest
  ) {
    this.userService.createUser(userCreateRequest);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public PageDTO<User> getUsers(
      @PageableDefault(sort = "username") Pageable pageable
  ) {
    return new PageDTO<>(this.userService.findUsers(pageable));
  }

  @PutMapping(path = "{id}/{status}")
  public void updateUserStatus(
      @PathVariable(name = "id") Integer id,
      @PathVariable(name = "status") UserStatus status
  ) {
    this.userService.updateStatus(id, status);
  }
}
