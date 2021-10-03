package com.codingandshare.tracking.controllers;

import com.codingandshare.tracking.domains.IssueMetaData;
import com.codingandshare.tracking.domains.User;
import com.codingandshare.tracking.dtos.ChangePassword;
import com.codingandshare.tracking.dtos.UserInfo;
import com.codingandshare.tracking.dtos.UserUpdateRequest;
import com.codingandshare.tracking.services.AuthenUserService;
import com.codingandshare.tracking.services.IssueMetaDataService;
import com.codingandshare.tracking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Define all endpoints relate to user
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@RestController
@RequestMapping("/tracking/user")
public class TrackingUserController {

  @Autowired
  private AuthenUserService authenUserService;

  @Autowired
  private UserService userService;

  @Autowired
  private IssueMetaDataService issueMetaDataService;

  @GetMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
  public User getUserInfo() {
    return this.authenUserService.getUserCurrentLogin();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<UserInfo> getUsers() {
    return this.userService.getUsers();
  }

  @PutMapping(
      path = "/changePassword",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public void changePassword(@RequestBody @Valid ChangePassword changePassword) {
    this.userService.changePassword(changePassword);
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public void updateUser(
      @RequestBody @Valid UserUpdateRequest userUpdateRequest
  ) {
    this.userService.updateUser(userUpdateRequest);
  }

  @GetMapping(
      path = "/metadata",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<IssueMetaData> getIssueMetaData() {
    return this.issueMetaDataService.findAll();
  }
}
