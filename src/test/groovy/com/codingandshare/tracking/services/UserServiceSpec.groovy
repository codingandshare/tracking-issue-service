package com.codingandshare.tracking.services

import com.codingandshare.tracking.BaseSpec
import com.codingandshare.tracking.domains.Role
import com.codingandshare.tracking.domains.User
import com.codingandshare.tracking.domains.UserStatus
import com.codingandshare.tracking.dtos.AuthenRequest
import com.codingandshare.tracking.dtos.ChangePassword
import com.codingandshare.tracking.dtos.UserCreateRequest
import com.codingandshare.tracking.dtos.UserInfo
import com.codingandshare.tracking.dtos.UserUpdateRequest
import com.codingandshare.tracking.exceptions.NotFoundException
import com.codingandshare.tracking.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles

/**
 * Testing for UserService
 *
 * @author Nhan Dinh
 * @since 10/31/2021
 */
@SpringBootTest
@ActiveProfiles('tests')
class UserServiceSpec extends BaseSpec {

  @Autowired
  private UserRepository userRepository

  @Autowired
  private UserService userService

  @Autowired
  private PasswordEncoder passwordEncoder

  @Autowired
  private AuthenUserService authenUserService

  def 'Verify get user endpoint successfully'() {
    given: 'Setup user'
    User user1 = new User(
        username: 'user_1',
        email: 'user@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: 'admin'
    )
    User user2 = new User(
        username: 'user_2',
        email: 'user2@gmail.com',
        status: UserStatus.INACTIVE,
        firstName: 'user_second',
        lastName: 'user_second',
        gender: 0,
        roles: [new Role(id: 1)],
        password: 'admin'
    )
    this.userRepository.saveAll([user1, user2])

    when: 'Get users'
    List<UserInfo> userInfos = this.userService.getUsers()

    then: 'Result as expected'
    noExceptionThrown()
    userInfos.size() == 2
    userInfos*.id == [1, user1.id]
    userInfos*.username == ['admin', 'user_1']
    userInfos*.firstName == ['Dinh', 'user_first']
    userInfos*.lastName == ['Nhan', 'user_last']

    cleanup:
    this.userRepository.deleteById(user1.id)
    this.userRepository.deleteById(user2.id)
  }

  def 'Verify change password when user not found in db'() {
    given: 'Setup object change password'
    ChangePassword changePassword = new ChangePassword(
        oldPassword: '123',
        newPassword: '1234'
    )

    and: 'Setup user'
    User user1 = new User(
        username: 'user_1',
        email: 'user@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user1)

    and: 'login for the user'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'user_1',
        password: '123'
    )
    this.authenUserService.login(authenRequest)

    and: 'Delete user in db'
    this.userRepository.deleteById(user1.id)

    when: 'Change password'
    this.userService.changePassword(changePassword)

    then: 'Throw exception as expected'
    NotFoundException e = thrown(NotFoundException)
    e.message == 'user id not found'
  }

  def 'Verify change password when old password is correctly'() {
    given: 'Setup object change password'
    ChangePassword changePassword = new ChangePassword(
        oldPassword: '123',
        newPassword: 'new_password'
    )

    and: 'Setup user'
    User userInput = new User(
        username: 'user',
        email: 'user@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(userInput)

    and: 'login for the user'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'user',
        password: '123'
    )
    this.authenUserService.login(authenRequest)

    when: 'Change password'
    this.userService.changePassword(changePassword)

    then: 'Result as expect'
    noExceptionThrown()
    Optional<User> userOptional = this.userRepository.findUserByUsername(authenRequest.username)
    User user = userOptional.orElse(null)
    user

    and: 'Password changed as expect'
    this.passwordEncoder.matches(changePassword.newPassword, user.password)

    cleanup:
    this.userRepository.deleteById(userInput.id)
  }

  def 'Verify change password when old password is incorrect'() {
    given: 'Setup object change password with old password is incorrect'
    ChangePassword changePassword = new ChangePassword(
        oldPassword: 'admin',
        newPassword: 'new_password'
    )

    and: 'Setup user'
    User userInput = new User(
        username: 'user',
        email: 'user@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(userInput)

    and: 'login for the user'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'user',
        password: '123'
    )
    this.authenUserService.login(authenRequest)

    when: 'Change password'
    this.userService.changePassword(changePassword)

    then: 'Throw exception as expected'
    IllegalArgumentException e = thrown(IllegalArgumentException)
    e.message == 'Old password is incorrect'

    cleanup:
    this.userRepository.deleteById(userInput.id)
  }

  def 'Verify update user current login successfully'() {
    given: 'Setup user'
    User userInput = new User(
        username: 'user',
        email: 'user@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(userInput)

    and: 'Setup authen request'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'user',
        password: '123'
    )
    this.authenUserService.login(authenRequest)

    and: 'Setup user update'
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        firstName: 'Dinh',
        lastName: 'Nhan',
        email: 'test@gmail.com',
        gender: 0,
    )

    when: 'update user'
    this.userService.updateUser(userUpdateRequest)

    then: 'Result as expect'
    noExceptionThrown()
    Optional<User> userOptional = this.userRepository.findById(userInput.id)
    User user = userOptional.orElse(null)
    user

    and: 'User updated'
    user.firstName == userUpdateRequest.firstName
    user.lastName == userUpdateRequest.lastName
    user.gender == userUpdateRequest.gender
    user.email == userUpdateRequest.email
    user.username == userInput.username
    user.password == userInput.password
    user.roles*.id == userInput.roles*.id

    cleanup:
    this.userRepository.deleteById(userInput.id)
  }

  def 'Verify create user when username is existed'() {
    given: 'Setup user create with username is existed'
    UserCreateRequest userCreateRequest = new UserCreateRequest(
        username: 'admin',
        password: 'admin',
        firstName: 'first_name',
        lastName: 'last_name',
        email: 'admin@gmail.com',
        gender: 1
    )

    and: 'Setup login user'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'admin',
        password: 'admin'
    )
    this.authenUserService.login(authenRequest)

    when: 'Create user'
    this.userService.createUser(userCreateRequest)

    then: 'Throw exception as expected'
    IllegalArgumentException e = thrown(IllegalArgumentException)
    e.message == 'Username is existed'
  }

  def 'Verify create user successfully'() {
    given: 'Setup user'
    UserCreateRequest userCreateRequest = new UserCreateRequest(
        username: 'user',
        password: '123',
        firstName: 'first_name',
        lastName: 'last_name',
        email: 'admin@gmail.com',
        gender: 1
    )

    and: 'Setup login user'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'admin',
        password: 'admin'
    )
    this.authenUserService.login(authenRequest)

    when: 'Create user'
    this.userService.createUser(userCreateRequest)

    then: 'Result as expected'
    noExceptionThrown()
    Optional<User> userOptional = this.userRepository.findUserByUsername('user')
    User user = userOptional.orElse(null)
    user

    and: 'User data as expected'
    user.username == userCreateRequest.username
    user.firstName == userCreateRequest.firstName
    user.lastName == userCreateRequest.lastName
    user.email == userCreateRequest.email
    user.gender == userCreateRequest.gender
    user.status == UserStatus.ACTIVE
    this.passwordEncoder.matches(userCreateRequest.password, user.password)
    user.roles*.roleName == ['ROLE_USER']

    cleanup:
    this.userRepository.deleteById(user.id)
  }

  def 'Verify find users by admin user when paging'() {
    given: 'Setup user'
    User userInput1 = new User(
        username: 'user1',
        email: 'user1@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_1',
        lastName: 'user_1',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    User userInput2 = new User(
        username: 'user2',
        email: 'user2@gmail.com',
        status: UserStatus.INACTIVE,
        firstName: 'user_2',
        lastName: 'user_2',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    User userInput3 = new User(
        username: 'user3',
        email: 'user3@gmail.com',
        status: UserStatus.INACTIVE,
        firstName: 'user_3',
        lastName: 'user_3',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    User userInput4 = new User(
        username: 'user4',
        email: 'user4@gmail.com',
        status: UserStatus.INACTIVE,
        firstName: 'user_4',
        lastName: 'user_4',
        gender: 0,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.saveAll([userInput1, userInput2, userInput3, userInput4])

    and: 'Login with admin user'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'admin',
        password: 'admin'
    )
    this.authenUserService.login(authenRequest)

    and: 'Setup pageable'
    Pageable pageable = PageRequest.of(pageInput, pageSizeInput, sortInput)

    when: 'Find users'
    Page<User> userPage = this.userService.findUsers(pageable)

    then: 'Result as expected'
    noExceptionThrown()
    userPage.totalElements == 5
    List<User> users = userPage.content
    users*.username == usernameExpect

    cleanup:
    this.userRepository.deleteAllById([userInput1, userInput2, userInput3, userInput4]*.id)

    where:
    pageInput | pageSizeInput | sortInput                                | usernameExpect
    0         | 5             | Sort.by(Sort.Direction.ASC, 'username')  | ['admin', 'user1', 'user2', 'user3', 'user4']
    0         | 5             | Sort.by(Sort.Direction.DESC, 'username') | ['user4', 'user3', 'user2', 'user1', 'admin']
    0         | 2             | Sort.by(Sort.Direction.DESC, 'username') | ['user4', 'user3']
    1         | 2             | Sort.by(Sort.Direction.DESC, 'username') | ['user2', 'user1']
    2         | 2             | Sort.by(Sort.Direction.DESC, 'username') | ['admin']
    3         | 2             | Sort.by(Sort.Direction.DESC, 'username') | []
    3         | 5             | Sort.by(Sort.Direction.DESC, 'username') | []
  }

  def 'Verify find users when access denied'() {
    given: 'setup user'
    User user = new User(
        username: 'user',
        email: 'user1@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_1',
        lastName: 'user_1',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user)

    and: 'Login with user role'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'user',
        password: '123'
    )
    this.authenUserService.login(authenRequest)

    when: 'Find users'
    this.userService.findUsers(PageRequest.of(1, 5))

    then: 'Throw exception as expected'
    AccessDeniedException ex = thrown(AccessDeniedException)
    ex.message == 'Access is denied'

    cleanup:
    this.userRepository.deleteById(user.id)
  }

  def 'Verify create user when access denied'() {
    given: 'setup user'
    User user = new User(
        username: 'user',
        email: 'user1@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_1',
        lastName: 'user_1',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user)

    and: 'Login with user role'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'user',
        password: '123'
    )
    this.authenUserService.login(authenRequest)

    and: 'Setup user request'
    UserCreateRequest userCreateRequest = new UserCreateRequest(
        username: 'user',
        password: '123',
        firstName: 'first_name',
        lastName: 'last_name',
        email: 'admin@gmail.com',
        gender: 1
    )

    when: 'Create user'
    this.userService.createUser(userCreateRequest)

    then: 'Throw exception as expected'
    AccessDeniedException ex = thrown(AccessDeniedException)
    ex.message == 'Access is denied'

    cleanup:
    this.userRepository.deleteById(user.id)
  }

  def 'Verify update user status success'() {
    given: 'setup user'
    User user = new User(
        username: 'user',
        email: 'user1@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_1',
        lastName: 'user_1',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user)

    and: 'Login with user role'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'admin',
        password: 'admin'
    )
    this.authenUserService.login(authenRequest)

    when: 'Update user status'
    this.userService.updateStatus(user.id, statusInput)

    then: 'Result as expected'
    noExceptionThrown()
    User userExpected = this.userRepository.findById(user.id).orElse(null)
    userExpected
    userExpected.status == statusInput
    userExpected.password == user.password
    userExpected.email == user.email
    userExpected.firstName == user.firstName
    userExpected.lastName == user.lastName
    userExpected.gender == user.gender
    userExpected.roles*.id == user.roles*.id

    cleanup:
    this.userRepository.deleteById(user.id)

    where:
    statusInput         | _
    UserStatus.ACTIVE   | _
    UserStatus.INACTIVE | _
  }

  def 'Verify update user status when access denied'() {
    given: 'setup user'
    User user = new User(
        username: 'user',
        email: 'user1@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_1',
        lastName: 'user_1',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user)

    and: 'Login with user role'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'user',
        password: '123'
    )
    this.authenUserService.login(authenRequest)

    when: 'Update user status'
    this.userService.updateStatus(user.id, UserStatus.ACTIVE)

    then: 'Throw exception as expected'
    AccessDeniedException ex = thrown(AccessDeniedException)
    ex.message == 'Access is denied'

    cleanup:
    this.userRepository.deleteById(user.id)
  }

  def 'Verify update user status when user id not found'() {
    given: 'Login user'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'admin',
        password: 'admin'
    )
    this.authenUserService.login(authenRequest)

    when: 'Update user status'
    this.userService.updateStatus(-1, UserStatus.ACTIVE)

    then: 'Throw exception as expected'
    NotFoundException ex = thrown(NotFoundException)
    ex.message == 'user id not found'
  }
}
