package com.codingandshare.tracking.controller

import com.codingandshare.tracking.BaseSpec
import com.codingandshare.tracking.components.JwtAuthenticationFilter
import com.codingandshare.tracking.configs.SecurityConfigure
import com.codingandshare.tracking.controllers.AuthenController
import com.codingandshare.tracking.dtos.AuthenRequest
import com.codingandshare.tracking.dtos.TokenResponse
import com.codingandshare.tracking.services.AuthenUserService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

/**
 * Unit test for AuthenController
 *
 * @author Nhan Dinh
 * @since 10/31/2021
 */
@WebMvcTest(
    controllers = AuthenController,
    excludeFilters = [
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SecurityConfigure),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JwtAuthenticationFilter),
    ]
)
@AutoConfigureMockMvc(addFilters = false)
class AuthenControllerSpec extends BaseSpec {

  @Autowired
  MockMvc mvc

  @SpringBean
  AuthenUserService authenUserService = Mock(AuthenUserService)

  def 'Verify login request when request body is invalid'() {
    given: 'Setup request body'
    AuthenRequest authenRequest = new AuthenRequest(
        username: usernameInput,
        password: passwordInput
    )

    String requestBody = MAPPER.writeValueAsString(authenRequest)

    when: 'Request to login endpoint'
    MockHttpServletResponse response = mvc.perform(
        post('/auth/login')
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
    usernameInput | passwordInput | messageExpected
    null          | '123'         | 'username is missing'
    ''            | '123'         | 'username is missing'
    'admin'       | ''            | 'password is missing'
    'admin'       | null          | 'password is missing'
  }

  def 'Verify request login endpoint response successfully'() {
    given: 'setup request body'
    AuthenRequest authenRequest = new AuthenRequest(
        username: 'admin',
        password: 'admin'
    )

    String requestBody = MAPPER.writeValueAsString(authenRequest)

    and: 'Mock data response'
    this.authenUserService.login(authenRequest) >> new TokenResponse(
        token: 'token'
    )

    when: 'Send request to login endpoint'
    MockHttpServletResponse response = mvc.perform(
        post('/auth/login')
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
    ).andReturn().response

    then: 'Response is ok'
    noExceptionThrown()
    response.status == HttpStatus.OK.value()

    and: 'Token as expected'
    Map responseBody = SLURPER.parseText(response.contentAsString) as Map
    responseBody.size() == 1
    responseBody.token == 'token'
  }
}
