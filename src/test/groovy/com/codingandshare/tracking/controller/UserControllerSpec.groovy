package com.codingandshare.tracking.controller

import com.codingandshare.tracking.BaseSpec
import com.codingandshare.tracking.components.JwtAuthenticationFilter
import com.codingandshare.tracking.configs.SecurityConfigure
import com.codingandshare.tracking.controllers.UserController
import com.codingandshare.tracking.domains.Role
import com.codingandshare.tracking.domains.User
import com.codingandshare.tracking.domains.UserStatus
import com.codingandshare.tracking.dtos.UserCreateRequest
import com.codingandshare.tracking.services.UserService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

/**
 * Testing for UserController
 *
 * @author Nhan Dinh
 * @since 10/31/2021
 */
@WebMvcTest(
    controllers = UserController,
    excludeFilters = [
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SecurityConfigure),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JwtAuthenticationFilter),
    ]
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerSpec extends BaseSpec {

  @Autowired
  MockMvc mvc

  @SpringBean
  UserService userService = Mock(UserService)

  def 'Verify create user when request body is invalid'() {
    given: 'Setup request body'
    UserCreateRequest userCreateRequest = new UserCreateRequest(
        username: usernameInput,
        password: passwordInput,
        firstName: firstNameInput,
        lastName: lastNameInput,
        email: emailInput,
        gender: genderInput
    )

    String requestBody = MAPPER.writeValueAsString(userCreateRequest)

    when: 'Send request to create user'
    MockHttpServletResponse response = mvc.perform(
        post('/user')
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
    ).andReturn().response

    then: 'Response as expected'
    noExceptionThrown()
    response.status == HttpStatus.BAD_REQUEST.value()

    and: 'Response body as expected'
    Map responseBody = SLURPER.parseText(response.contentAsString) as Map
    responseBody.size() == 1
    responseBody.message == messageExpected

    where:
    usernameInput | passwordInput | firstNameInput | lastNameInput | emailInput       | genderInput | messageExpected
    ''            | 'admin'       | 'first_name'   | 'last_name'   | 'user@gmail.com' | 1           | 'username is missing'
    null          | 'admin'       | 'first_name'   | 'last_name'   | 'user@gmail.com' | 1           | 'username is missing'
    'username'    | ''            | 'first_name'   | 'last_name'   | 'user@gmail.com' | 1           | 'password is missing'
    'username'    | null          | 'first_name'   | 'last_name'   | 'user@gmail.com' | 1           | 'password is missing'
    'username'    | 'admin'       | ''             | 'last_name'   | 'user@gmail.com' | 1           | 'firstName is missing'
    'username'    | 'admin'       | null           | 'last_name'   | 'user@gmail.com' | 1           | 'firstName is missing'
    'username'    | 'admin'       | 'first_name'   | ''            | 'user@gmail.com' | 1           | 'lastName is missing'
    'username'    | 'admin'       | 'first_name'   | null          | 'user@gmail.com' | 1           | 'lastName is missing'
    'username'    | 'admin'       | 'first_name'   | 'last_name'   | ''               | 1           | 'email is missing'
    'username'    | 'admin'       | 'first_name'   | 'last_name'   | null             | 1           | 'email is missing'
    'username'    | 'admin'       | 'first_name'   | 'last_name'   | 'user'           | 1           | 'email is invalid format'
  }

  def 'Verify create user successfully'() {
    given: 'Setup request body'
    UserCreateRequest userCreateRequest = new UserCreateRequest(
        username: 'admin',
        password: 'admin',
        firstName: 'first_name',
        lastName: 'last_name',
        email: 'admin@gmail.com',
        gender: 1
    )
    String requestBody = MAPPER.writeValueAsString(userCreateRequest)

    when: 'Send request to create user'
    MockHttpServletResponse response = mvc.perform(
        post('/user')
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
    ).andReturn().response

    then: 'Response as expected'
    noExceptionThrown()
    response.status == HttpStatus.CREATED.value()

    and: 'Response body as expected'
    response.contentAsString.isEmpty()
  }

  def 'Verify get user with paging'() {
    given: 'Setup and mock data'
    User user = new User(
        id: 1,
        username: 'user_1',
        email: 'user@gmail.com',
        status: UserStatus.ACTIVE,
        firstName: 'user_first',
        lastName: 'user_last',
        gender: 1,
        roles: [new Role(id: 1)],
        password: 'admin'
    )
    Pageable pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.asc('username')))
    Page<User> pageUser = new PageImpl<>(
        [user],
        pageRequest,
        1
    )
    this.userService.findUsers(pageRequest) >> pageUser

    when: 'Get user'
    MockHttpServletResponse response = mvc.perform(
        get('/user').contentType(MediaType.APPLICATION_JSON_VALUE)
    ).andReturn().response

    then: 'Response as expected'
    noExceptionThrown()
    response.status == HttpStatus.OK.value()

    and: 'Response body page as expected'
    Map responseBody = SLURPER.parseText(response.contentAsString) as Map
    responseBody.size() == 6
    responseBody.total == 1
    responseBody.sort == 'username: ASC'
    responseBody.totalPage == 1
    responseBody.page == 0
    responseBody.pageSize == 10
    responseBody.content

    and: 'Response list as expected'
    List<Map> users = responseBody.content as List<Map>
    users.size() == 1
    Map userExpected = users.first()
    userExpected.size() == 7
    userExpected.id == user.id
    userExpected.username == user.username
    userExpected.lastName == user.lastName
    userExpected.firstName == user.firstName
    userExpected.email == user.email
    userExpected.gender == user.gender
    userExpected.status == user.status.name()
  }

  def 'Verify update status for user when status is invalid'() {
    when: 'Send request to update status user'
    MockHttpServletResponse response = mvc.perform(
        put('/user/1/STATUS_INVALID')
            .contentType(MediaType.APPLICATION_JSON_VALUE)
    ).andReturn().response

    then: 'Response as expected'
    noExceptionThrown()
    response.status == HttpStatus.BAD_REQUEST.value()

    and: 'Response message as expected'
    Map responseBody = SLURPER.parseText(response.contentAsString) as Map
    responseBody.size() == 1
    responseBody.message == 'User Status is invalid'
  }

  def 'Verify update user status successfully'() {
    when: 'Send request to update status user'
    MockHttpServletResponse response = mvc.perform(
        put("/user/1/$statusInput")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
    ).andReturn().response

    then: 'Response as expected'
    noExceptionThrown()
    response.status == HttpStatus.OK.value()

    and: 'Response body as expected'
    response.contentAsString.isEmpty()

    where:
    statusInput                | _
    UserStatus.ACTIVE.name()   | _
    UserStatus.INACTIVE.name() | _
  }
}
