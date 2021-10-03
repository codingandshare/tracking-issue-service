package com.codingandshare.tracking.services;

import com.codingandshare.tracking.annotations.IsRoleAdmin;
import com.codingandshare.tracking.domains.Role;
import com.codingandshare.tracking.domains.User;
import com.codingandshare.tracking.domains.UserStatus;
import com.codingandshare.tracking.dtos.ChangePassword;
import com.codingandshare.tracking.dtos.UserInfo;
import com.codingandshare.tracking.dtos.UserCreateRequest;
import com.codingandshare.tracking.dtos.UserUpdateRequest;
import com.codingandshare.tracking.exceptions.NotFoundException;
import com.codingandshare.tracking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handle business logic relate to user
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AuthenUserService authenUserService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * Get list user info
   *
   * @return {@link List< UserInfo >}
   */
  public List<UserInfo> getUsers() {
    List<User> users = this.userRepository.findAll();
    return users.stream().map(it -> UserInfo
        .builder()
        .username(it.getUsername())
        .id(it.getId())
        .firstName(it.getFirstName())
        .lastName(it.getLastName()).build()).collect(Collectors.toList());
  }

  public void changePassword(ChangePassword changePassword) {
    User currentUser = this.authenUserService.getUserCurrentLogin();
    Optional<User> userOptional = this.userRepository.findById(currentUser.getId());
    User userUpdating = userOptional.orElseThrow(() -> {
      throw new NotFoundException("user id not found");
    });
    if (this.passwordEncoder.matches(changePassword.getOldPassword(), userUpdating.getPassword())) {
      userUpdating.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
      this.userRepository.save(userUpdating);
    } else {
      throw new IllegalArgumentException("Old password is incorrect");
    }
  }

  public void updateUser(UserUpdateRequest userUpdateRequest) {
    User user = this.authenUserService.getUserCurrentLogin();
    user.setFirstName(userUpdateRequest.getFirstName());
    user.setLastName(userUpdateRequest.getLastName());
    user.setEmail(userUpdateRequest.getEmail());
    user.setGender(userUpdateRequest.getGender());
    this.userRepository.save(user);
  }

  @IsRoleAdmin
  public void createUser(UserCreateRequest userRequest) {
    Optional<User> userOptional = this.userRepository.findUserByUsername(userRequest.getUsername());
    userOptional.orElseThrow(() -> {
      throw new IllegalArgumentException("Username is existed");
    });
    User user = new User();
    user.setUsername(userRequest.getUsername());
    user.setPassword(this.passwordEncoder.encode(userRequest.getPassword()));
    user.setFirstName(userRequest.getFirstName());
    user.setLastName(userRequest.getLastName());
    user.setEmail(userRequest.getEmail());
    user.setGender(userRequest.getGender());
    Set<Role> roles = new HashSet<>();
    Role userRole = new Role();
    userRole.setId(1);
    roles.add(userRole);
    user.setRoles(roles);
    this.userRepository.save(user);
  }

  @IsRoleAdmin
  public Page<User> findUsers(Pageable pageable) {
    return this.userRepository.findAll(pageable);
  }

  public void updateStatus(Integer id, UserStatus userStatus) {
    Optional<User> userOptional = this.userRepository.findById(id);
    User userUpdating = userOptional.orElseThrow(() -> {
      throw new NotFoundException("user id not found");
    });
    userUpdating.setStatus(userStatus);
    this.userRepository.save(userUpdating);
  }

}
