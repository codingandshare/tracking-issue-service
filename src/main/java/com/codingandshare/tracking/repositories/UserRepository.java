package com.codingandshare.tracking.repositories;

import com.codingandshare.tracking.domains.User;
import com.codingandshare.tracking.domains.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * The class access db relate to user table
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
public interface UserRepository extends JpaRepository<User, Integer> {

  /**
   * Method query database from user table by username
   *
   * @param username need to get
   * @return {@link Optional<User>}
   */
  Optional<User> findUserByUsername(String username);

  /**
   * Get list user by status user
   *
   * @param status
   * @return List<User>
   */
  List<User> findUserByStatusOrderByUsernameAsc(UserStatus status);
}
