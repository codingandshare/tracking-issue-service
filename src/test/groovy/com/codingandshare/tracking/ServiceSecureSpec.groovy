package com.codingandshare.tracking

import com.codingandshare.tracking.components.JwtTokenProvider
import com.codingandshare.tracking.domains.Role
import com.codingandshare.tracking.domains.User
import com.codingandshare.tracking.domains.UserStatus
import com.codingandshare.tracking.dtos.AuthenRequest
import com.codingandshare.tracking.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles

/**
 * Testing for authentication
 *
 * @author Nhan Dinh
 * @since 10/24/21
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles('tests')
class ServiceSecureSpec extends BaseSpec {

  private static final String AUTHENTICATION_HEADER = 'Authorization'

  @Autowired
  TestRestTemplate testRestTemplate

  @Autowired
  JwtTokenProvider jwtTokenProvider

  @Autowired
  UserRepository userRepository

  @Autowired
  PasswordEncoder passwordEncoder

  def 'Verify login success'() {
    given: 'Setup data'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'admin',
        password: 'admin'
    )
    HttpEntity<AuthenRequest> request = new HttpEntity<>(authenRequest, HEADERS)

    when: 'Request to login endpoint'
    ResponseEntity<String> response =
        this.testRestTemplate.postForEntity(new URI('/auth/login'), request, String)

    then: 'Response as expect'
    noExceptionThrown()
    response.getStatusCode() == HttpStatus.OK

    and: 'Response body as expect'
    Map responseBody = SLURPER.parseText(response.getBody()) as Map
    responseBody.size() == 1

    and: 'Token is valid'
    this.jwtTokenProvider.validateToken(responseBody.token as String)
  }

  def 'Verify login failed'() {
    given: 'Setup data'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'admin',
        password: 'pass_invalid'
    )
    HttpEntity<AuthenRequest> request = new HttpEntity<>(authenRequest, HEADERS)

    when: 'Request to login endpoint'
    ResponseEntity<String> responseEntity = this.testRestTemplate.postForEntity(new URI('/auth/login'), request, String)

    then: 'Response as expect'
    noExceptionThrown()
    responseEntity.statusCode == HttpStatus.UNAUTHORIZED

    and: 'Response body as expect'
    Map responseBody = SLURPER.parseText(responseEntity.getBody()) as Map
    responseBody.size() == 1
    responseBody.message == 'Bad credentials'
  }

  def 'Verify user with role admin to access resources'() {
    given: 'Setup token'
    String token = this.jwtTokenProvider.generateToken('admin')
    HEADERS.add(AUTHENTICATION_HEADER, "Bearer $token")
    RequestEntity<String> requestEntityAdmin = new RequestEntity<>(
        HEADERS, HttpMethod.GET, new URI('/user')
    )
    RequestEntity<String> requestEntityUser = new RequestEntity<>(
        HEADERS, HttpMethod.GET, new URI('/tracking/version')
    )

    when: 'Send request to admin resource'
    ResponseEntity<String> responseAdminResource = this.testRestTemplate.exchange(requestEntityAdmin, String)

    and: 'Send request to user resource'
    ResponseEntity<String> responseUserResource = this.testRestTemplate.exchange(requestEntityUser, String)

    then: 'Response status for requests is ok'
    noExceptionThrown()
    responseAdminResource.statusCode == HttpStatus.OK
    responseUserResource.statusCode == HttpStatus.OK

    cleanup:
    HEADERS.remove(AUTHENTICATION_HEADER)
  }

  def 'Verity user with role user to access resources'() {
    given: 'Setup user with role user'
    User user = new User(
        username: 'user_1',
        email: 'user@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user)

    and: 'Setup request for user, admin roles'
    String token = this.jwtTokenProvider.generateToken(user.username)
    HEADERS.add(AUTHENTICATION_HEADER, "Bearer $token")
    RequestEntity<String> requestEntityAdmin = new RequestEntity<>(
        HEADERS, HttpMethod.GET, new URI('/user')
    )
    RequestEntity<String> requestEntityUser = new RequestEntity<>(
        HEADERS, HttpMethod.GET, new URI('/tracking/version')
    )

    when: 'Request to resource with admin role'
    ResponseEntity<String> responseAdminResource = this.testRestTemplate.exchange(requestEntityAdmin, String)

    and: 'Request to resource with user role'
    ResponseEntity<String> responseUserResource = this.testRestTemplate.exchange(requestEntityUser, String)

    then: 'Response status ok with resource user'
    noExceptionThrown()
    responseUserResource.statusCode == HttpStatus.OK

    and: 'Response status access denied with resource admin'
    responseAdminResource.statusCode == HttpStatus.FORBIDDEN
    Map responseBody = SLURPER.parseText(responseAdminResource.getBody()) as Map
    responseBody.size() == 1
    responseBody.message == 'Access is denied'

    cleanup:
    HEADERS.remove(AUTHENTICATION_HEADER)
    this.userRepository.deleteById(user.id)
  }

  def 'Verify request to resource when user is inactive'() {
    given: 'Setup user data is inactive'
    User user = new User(
        username: 'user_1',
        email: 'user@gmail.com',
        status: UserStatus.INACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user)

    and: 'Setup request for user role'
    String token = this.jwtTokenProvider.generateToken(user.username)
    HEADERS.add(AUTHENTICATION_HEADER, "Bearer $token")
    RequestEntity<String> requestEntityUser = new RequestEntity<>(
        HEADERS, HttpMethod.GET, new URI('/tracking/version')
    )

    when: 'Request to resource with user role'
    ResponseEntity<String> responseUserResource = this.testRestTemplate.exchange(requestEntityUser, String)

    then: 'Response status is unauthorized'
    noExceptionThrown()
    responseUserResource.statusCode == HttpStatus.UNAUTHORIZED

    and: 'Response body is expected'
    Map responseBody = SLURPER.parseText(responseUserResource.getBody()) as Map
    responseBody.size() == 1
    responseBody.message == 'Full authentication is required to access this resource'

    cleanup:
    HEADERS.remove(AUTHENTICATION_HEADER)
    this.userRepository.deleteById(user.id)
  }

  def 'Verify login when user is inactive'() {
    given: 'Setup user data is inactive'
    User user = new User(
        username: 'user_1',
        email: 'user@gmail.com',
        status: UserStatus.INACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: this.passwordEncoder.encode('123')
    )
    this.userRepository.save(user)

    and: 'Setup request body'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'user_1',
        password: '123'
    )
    HttpEntity<AuthenRequest> request = new HttpEntity<>(authenRequest, HEADERS)

    when: 'Request to login endpoint'
    ResponseEntity<String> responseEntity = this.testRestTemplate.postForEntity(new URI('/auth/login'), request, String)

    then: 'Response as expect'
    noExceptionThrown()
    responseEntity.statusCode == HttpStatus.UNAUTHORIZED

    and: 'Response body as expect'
    Map responseBody = SLURPER.parseText(responseEntity.getBody()) as Map
    responseBody.size() == 1
    responseBody.message == 'Bad credentials'

    cleanup:
    this.userRepository.deleteById(user.id)
  }
}
