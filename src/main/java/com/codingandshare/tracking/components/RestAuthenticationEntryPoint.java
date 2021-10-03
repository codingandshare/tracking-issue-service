package com.codingandshare.tracking.components;

import com.codingandshare.tracking.dtos.ResponseObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom response json when un-authentication
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Slf4j
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Autowired
  @Qualifier("objectMapperJson")
  private ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException
  ) throws IOException, ServletException {
    log.error("Responding with unauthorized error.", authException);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ResponseObject responseObject = new ResponseObject();
    responseObject.setMessage(authException.getMessage());
    response.getOutputStream().print(this.objectMapper.writeValueAsString(responseObject));
  }
}
