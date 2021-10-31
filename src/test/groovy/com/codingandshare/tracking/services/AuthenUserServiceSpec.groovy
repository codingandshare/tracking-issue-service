package com.codingandshare.tracking.services

import com.codingandshare.tracking.BaseSpec
import com.codingandshare.tracking.components.JwtTokenProvider
import com.codingandshare.tracking.domains.Role
import com.codingandshare.tracking.domains.User
import com.codingandshare.tracking.domains.UserStatus
import com.codingandshare.tracking.dtos.AuthenRequest
import com.codingandshare.tracking.dtos.TokenResponse
import com.codingandshare.tracking.exceptions.NotFoundException
import com.codingandshare.tracking.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles

/**
 * Testing for AuthenUserService
 *
 * @author Nhan Dinh
 * @since 10/31/2021
 */
@SpringBootTest
@ActiveProfiles('tests')
class AuthenUserServiceSpec extends BaseSpec {

  @Autowired
  UserRepository userRepository

  @Autowired
  AuthenUserService authenUserService

  @Autowired
  PasswordEncoder passwordEncoder

  @Autowired
  JwtTokenProvider jwtTokenProvider

  @Autowired
  private AuthenticationManager authenticationManager;

  def 'Verify load user when user not found or is inactive'() {
    given: 'Setup user is inactive'
    User user = new User(
        username: 'user_inactive',
        email: 'user@gmail.com',
        status: UserStatus.INACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user)

    when: 'Load user from database'
    this.authenUserService.loadUserByUsername(usernameInput)

    then: 'Throw exception as expected'
    UsernameNotFoundException e = thrown(UsernameNotFoundException)
    e.message == messageExpect

    cleanup:
    this.userRepository.deleteById(user.id)

    where:
    usernameInput   | messageExpect
    'user_fake'     | 'User user_fake not found'
    'user_inactive' | 'User is disabled'
  }

  def 'Verify load user successfully'() {
    given: 'Setup user'
    User user = new User(
        username: 'user',
        email: 'user@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user)

    when: 'Load user'
    UserDetails userDetails = this.authenUserService.loadUserByUsername('user')

    then: 'Result as expected'
    noExceptionThrown()
    userDetails.username == user.username
    userDetails.authorities*.getAuthority() == ['ROLE_USER']
    userDetails.password == user.password
    userDetails.isAccountNonExpired()
    userDetails.isAccountNonLocked()
    userDetails.isCredentialsNonExpired()
    userDetails.isEnabled()

    cleanup:
    this.userRepository.deleteById(user.id)
  }

  def 'Verify login successfully'() {
    given: 'Setup authen request'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'admin',
        password: 'admin'
    )

    when: 'Login with username, password'
    TokenResponse tokenResponse = this.authenUserService.login(authenRequest)

    then: 'Result as expected'
    noExceptionThrown()
    this.jwtTokenProvider.validateToken(tokenResponse.token)

    and: 'Security context with authentication as expected'
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
    authentication != null
    UserDetails userDetails = authentication.getPrincipal() as UserDetails

    and: 'User details as expected'
    userDetails.username == 'admin'
    userDetails.authorities*.getAuthority() == ['ROLE_USER', 'ROLE_ADMIN']
    userDetails.password
    userDetails.isAccountNonExpired()
    userDetails.isAccountNonLocked()
    userDetails.isCredentialsNonExpired()
    userDetails.isEnabled()
  }

  def 'Verify login failed'() {
    given: 'Setup authen request'
    AuthenRequest authenRequest = new AuthenRequest(
        username: usernameInput,
        password: passwordInput
    )

    when: 'Login with username, password'
    this.authenUserService.login(authenRequest)

    then: 'Throw exception as expect'
    BadCredentialsException e = thrown(BadCredentialsException)
    e.message == 'Bad credentials'

    where:
    usernameInput    | passwordInput
    'admin'          | 'password_incorrect'
    'user_incorrect' | 'admin'
    'user_incorrect' | 'password_incorrect'
  }

  def 'Verify login when user is disabled'() {
    given: 'Setup user is inactive'
    User user = new User(
        username: 'user',
        email: 'user@gmail.com',
        status: UserStatus.INACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user)

    and: 'Setup authen request'
    AuthenRequest authenRequest = new AuthenRequest(
        username: user.username,
        password: '123'
    )

    when: 'Login with username, password'
    this.authenUserService.login(authenRequest)

    then: 'Throw exception as expect'
    BadCredentialsException e = thrown(BadCredentialsException)
    e.message == 'Bad credentials'

    cleanup:
    this.userRepository.deleteById(user.id)
  }

  def 'Verify get user current login successfully'() {
    given: 'Setup authentication security context'
    Authentication authentication = this.authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            'admin',
            'admin'
        )
    )
    SecurityContextHolder.getContext().setAuthentication(authentication)

    when: 'Get user current login'
    User user = this.authenUserService.getUserCurrentLogin()

    then: 'Result as expected'
    noExceptionThrown()
    user.username == 'admin'
    user.roles*.roleName == ['ROLE_USER', 'ROLE_ADMIN']
    user.firstName == 'Dinh'
    user.lastName == 'Nhan'
    user.email == 'huunhancit@gmail.com'
    user.gender == 1
    user.status == UserStatus.ACTIVE
    this.passwordEncoder.matches('admin', user.password)
  }

  def 'Verify get user current failed'() {
    when: 'Get user current login'
    this.authenUserService.getUserCurrentLogin()

    then: 'Result as expected'
    NotFoundException e = thrown(NotFoundException)
    e.message == 'User not found'
  }
}
