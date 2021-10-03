package com.codingandshare.tracking.controllers;

import com.codingandshare.tracking.dtos.AuthenRequest;
import com.codingandshare.tracking.dtos.TokenResponse;
import com.codingandshare.tracking.services.AuthenUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@RestController
@RequestMapping(value = "/auth")
public class AuthenController {

  @Autowired
  private AuthenUserService authenUserService;

  @PostMapping(
      path = "/login",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public TokenResponse login(@RequestBody @Valid AuthenRequest authenRequest) {
    return this.authenUserService.login(authenRequest);
  }
}
